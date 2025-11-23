<template>
  <div class="pdf-editor-container">
    <div class="pdf-controls">
      <button 
        class="recompile-btn"
        :disabled="isRecompiling"
      >
        {{ isRecompiling ? 'Generating PDF...' : 'Recompile & Download PDF' }}
      </button>
    </div>
    <div class="pdf-viewer-container">
      <div v-for="page in pages" :key="page.pageNumber" class="pdf-page-wrapper">
        <!-- Canvas renders the original PDF background (images, vectors) -->
        <canvas :id="`pdf-canvas-${page.pageNumber}`"></canvas>
        <!-- Text Layer contains editable divs overlaying the canvas -->
        <div :id="`text-layer-${page.pageNumber}`" class="text-layer"></div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, nextTick } from 'vue';
import { usePdfStore } from '@/stores/pdf';
import { usePdfApi } from '@/composables/usePdfApi';
import * as pdfjsLib from 'pdfjs-dist';

// Ensure worker is set up
pdfjsLib.GlobalWorkerOptions.workerSrc = `/pdf.worker.min.mjs`;

const pdfStore = usePdfStore();
const pdfApi = usePdfApi();

const pages = ref<any[]>([]);
const isRecompiling = ref(false);

// Font fallback function (Same as your original)
const getFontFamilyWithFallback = (fontName: string): string => {
  const lowerFontName = fontName.toLowerCase();
  const fontMappings: Record<string, string> = {
    'arial': 'Arial, sans-serif',
    'helvetica': 'Helvetica, Arial, sans-serif',
    'times': '"Times New Roman", serif',
    'courier': '"Courier New", monospace',
  };
  
  for (const [pattern, fallback] of Object.entries(fontMappings)) {
    if (lowerFontName.includes(pattern)) return `"${fontName}", ${fallback}`;
  }
  
  if (lowerFontName.includes('serif')) return `"${fontName}", "Merriweather", serif`;
  if (lowerFontName.includes('mono')) return `"${fontName}", "JetBrains Mono", monospace`;
  
  return `"${fontName}", "Open Sans", sans-serif`;
};

onMounted(async () => {
  if (pdfStore.file) {
    try {
      const textElements = await pdfApi.extractTextFromPdf(pdfStore.file);
      
      const fileReader = new FileReader();
      fileReader.onload = async () => {
        const typedArray = new Uint8Array(fileReader.result as ArrayBuffer);
        const loadingTask = pdfjsLib.getDocument(typedArray);
        const pdfDocument = await loadingTask.promise;

        pages.value = Array.from({ length: pdfDocument.numPages }, (_, i) => ({
          pageNumber: i + 1,
          pdfPage: null,
        }));

        await nextTick();

        for (let i = 1; i <= pdfDocument.numPages; i++) {
          const page = await pdfDocument.getPage(i);
          const viewport = page.getViewport({ scale: 1.5 });

          const canvas = document.getElementById(`pdf-canvas-${i}`) as HTMLCanvasElement;
          const textLayerDiv = document.getElementById(`text-layer-${i}`) as HTMLDivElement;
          
          canvas.height = viewport.height;
          canvas.width = viewport.width;
          
          textLayerDiv.style.width = `${viewport.width}px`;
          textLayerDiv.style.height = `${viewport.height}px`;

          // Render Canvas (Background)
          const ctx = canvas.getContext('2d')!;
          // Hide text in canvas rendering to prevent double-vision
          // (We want the canvas to only show lines/images, and the DIVs to show text)
          const originalFillText = ctx.fillText;
          const originalStrokeText = ctx.strokeText;
          ctx.fillText = function() {}; 
          ctx.strokeText = function() {};
          
          await page.render({
            canvasContext: ctx,
            viewport: viewport
          }).promise;
          
          ctx.fillText = originalFillText;
          ctx.strokeText = originalStrokeText;

          // Render DOM Text Layer
          const pageTextElements = textElements.filter(el => el.pageNumber === i);

          pageTextElements.forEach((textElement) => {
            const textDiv = document.createElement('div');
            
            // Calculate styles exactly as you did
            const pdfFontSize = textElement.fontSize * viewport.scale;
            const pdfX = textElement.x * viewport.scale;
            const pdfY = textElement.y * viewport.scale; 
            const baselineAdjustment = pdfFontSize * 0.75; 
            const finalTop = pdfY - baselineAdjustment;

            textDiv.innerText = textElement.text;
            textDiv.contentEditable = 'true';
            
            textDiv.style.position = 'absolute';
            textDiv.style.left = `${pdfX}px`;
            textDiv.style.top = `${finalTop}px`;
            textDiv.style.fontSize = `${pdfFontSize}px`;
            textDiv.style.fontFamily = getFontFamilyWithFallback(textElement.fontFamily);
            textDiv.style.fontWeight = `${textElement.fontWeight}`;
            textDiv.style.color = textElement.color || '#000000'; 
            textDiv.style.lineHeight = '1'; 
            textDiv.style.whiteSpace = 'pre';
            textDiv.style.transformOrigin = '0% 50%';

            textLayerDiv.appendChild(textDiv);

            // Calculate Width & ScaleX
            const pdfWidth = textElement.width * viewport.scale;
            const renderedWidth = textDiv.getBoundingClientRect().width;
            
            if (renderedWidth > 0 && pdfWidth > 0) {
               const scaleFactor = pdfWidth / renderedWidth;
               if (Math.abs(1 - scaleFactor) > 0.01) {
                   textDiv.style.transform = `scaleX(${scaleFactor})`;
               }
            }
          });
        }
      };
      fileReader.readAsArrayBuffer(pdfStore.file);
    } catch (error) {
      console.error('Error:', error);
    }
  }
});
</script>

<style scoped>
.pdf-editor-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.pdf-controls {
  padding: 16px;
  border-bottom: 1px solid #e5e7eb;
  background: white;
  display: flex;
  justify-content: center;
  gap: 10px;
}

.recompile-btn {
  background: #3b82f6;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
}

.recompile-btn:hover:not(:disabled) {
  background: #2563eb;
}

.recompile-btn:disabled {
  background: #9ca3af;
  cursor: not-allowed;
}

.pdf-viewer-container {
  margin: 0 auto;
  flex: 1;
  overflow: auto;
  position: relative;
  background: #f3f4f6; /* Light gray background for contrast */
  padding: 20px;
}

.pdf-page-wrapper {
  position: relative; 
  margin-bottom: 20px; 
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  background: white;
}

.text-layer {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  /* Ensure text layer doesn't block canvas pointer events if needed, 
     though for editing text, pointer-events needs to be auto (default) */
}
</style>