import { ref, Ref } from 'vue'
import { uploadPdf, getPdfInfo, extractTextFromPdf, type PdfTextElement } from '@/generated/api'
export type { PdfTextElement }

export interface UsePdfApiReturn {
  loading: Ref<boolean>
  error: Ref<string | null>
  uploadPdf: (file: File) => Promise<any>
  getPdfInfo: (id: string) => Promise<any>
  extractTextFromPdf: (file: File) => Promise<PdfTextElement[]>
}

export function usePdfApi(): UsePdfApiReturn {
  const loading = ref(false)
  const error = ref<string | null>(null)

  const uploadPdfFn = async (file: File) => {
    loading.value = true
    error.value = null

    try {
      const response = await uploadPdf({ body: { file } })
      return response.data
    } catch (err: any) {
      error.value = err.message || 'Failed to upload PDF'
      throw err
    } finally {
      loading.value = false
    }
  }

  const getPdfInfoFn = async (id: string) => {
    loading.value = true
    error.value = null

    try {
      const response = await getPdfInfo({ path: { id } })
      return response.data
    } catch (err: any) {
      error.value = err.message || 'Failed to get PDF info'
      throw err
    } finally {
      loading.value = false
    }
  }

  const extractTextFromPdfFn = async (file: File): Promise<PdfTextElement[]> => {
    loading.value = true
    error.value = null
    try {
      const response = await extractTextFromPdf({ body: { file } });
      return response.data || [];
    } catch (err: any) {
      error.value = err.message || 'Failed to extract text from PDF';
      throw err;
    } finally {
      loading.value = false;
    }
  };

  return {
    loading,
    error,
    uploadPdf: uploadPdfFn,
    getPdfInfo: getPdfInfoFn,
    extractTextFromPdf: extractTextFromPdfFn,
  }
}