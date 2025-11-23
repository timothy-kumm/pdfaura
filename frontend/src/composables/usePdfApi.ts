import { ref, Ref } from 'vue'
import { PDFOperationsApi, type PdfTextElement } from '@/generated/api'
export type { PdfTextElement }
import { apiConfig } from '@/services/api'

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
  const api = new PDFOperationsApi(apiConfig)

  const uploadPdf = async (file: File) => {
    loading.value = true
    error.value = null

    try {
      const formData = new FormData()
      formData.append('file', file)

      const response = await api.uploadPdf(file)
      return response.data
    } catch (err: any) {
      error.value = err.message || 'Failed to upload PDF'
      throw err
    } finally {
      loading.value = false
    }
  }

  const getPdfInfo = async (id: string) => {
    loading.value = true
    error.value = null

    try {
      const response = await api.getPdfInfo(id)
      return response.data
    } catch (err: any) {
      error.value = err.message || 'Failed to get PDF info'
      throw err
    } finally {
      loading.value = false
    }
  }

  const extractTextFromPdf = async (file: File): Promise<PdfTextElement[]> => {
    loading.value = true
    error.value = null
    try {
      const response = await api.extractTextFromPdf(file);
      return response.data;
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
    uploadPdf,
    getPdfInfo,
    extractTextFromPdf,
  }
}