<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { usePdfStore } from '@/stores/pdf';

const router = useRouter();
const selectedFile = ref<File | null>(null);
const pdfStore = usePdfStore();

function handleFileChange(event: Event) {
  const target = event.target as HTMLInputElement;
  if (target.files && target.files.length > 0) {
    selectedFile.value = target.files[0];
    if (selectedFile.value) {
      pdfStore.setFile(selectedFile.value);
      router.push({ name: 'pdf-editor' });
    }
  }
}

function handleFileDrop(event: DragEvent) {
  if (event.dataTransfer && event.dataTransfer.files && event.dataTransfer.files.length > 0) {
    const file = event.dataTransfer.files[0];
    if (file.type === 'application/pdf') {
      selectedFile.value = file;
      if (selectedFile.value) {
        pdfStore.setFile(selectedFile.value);
        router.push({ name: 'pdf-editor' });
      }
    }
  }
}
</script>

<template>
  <div class="flex items-center justify-center h-full">
    <Card class="w-[90%] md:w-[70%] lg:w-[75%] lg:max-w-screen-xl">
      <CardHeader>
        <CardTitle class="text-2xl font-bold text-center">PDF Magic</CardTitle>
        <CardDescription class="text-center">
          Upload your PDF file and let the magic begin!
        </CardDescription>
      </CardHeader>
      <CardContent>
        <div class="grid w-full items-center gap-4">
          <div class="flex flex-col space-y-1.5">
            <div class="flex items-center justify-center w-full" @dragover.prevent @drop.prevent="handleFileDrop">
              <label for="pdf-upload"
                class="flex flex-col items-center justify-center w-full h-64 border-2 border-dashed rounded-lg cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-800">
                <div class="flex flex-col items-center justify-center pt-5 pb-6">
                  <svg class="w-8 h-8 mb-4 text-gray-500 dark:text-gray-400" aria-hidden="true"
                    xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 20 16">
                    <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M13 13h3a3 3 0 0 0 0-6h-.025A5.56 5.56 0 0 0 16 6.5 5.5 5.5 0 0 0 5.207 5.021C5.137 5.017 5.071 5 5 5a4 4 0 0 0 0 8h2.167M10 15V6m0 0L8 8m2-2 2 2" />
                  </svg>
                  <p class="mb-2 text-sm text-gray-500 dark:text-gray-400"><span class="font-semibold">Click to
                      upload</span> or drag and drop</p>
                  <p class="text-xs text-gray-500 dark:text-gray-400">PDF (MAX. 800x400px)</p>
                </div>
                <Input id="pdf-upload" type="file" class="hidden" @change="handleFileChange" accept=".pdf" />
              </label>
            </div>
          </div>
        </div>
        <div v-if="selectedFile" class="mt-4 text-center">
          <p>Selected file: {{ selectedFile.name }}</p>
        </div>
      </CardContent>
    </Card>
  </div>
</template>