import axios, { AxiosInstance } from 'axios'
import { client } from '@/generated/api/client.gen'

// Base API configuration
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

// Create axios instance with default configuration
export const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  // Don't set default Content-Type - let axios handle it based on request body
  // This allows multipart/form-data for file uploads and application/json for regular requests
})

// Configure the generated client to use our axios instance
client.setConfig({
  axios: apiClient,
  baseURL: API_BASE_URL,
})

// Request interceptor for authentication
apiClient.interceptors.request.use(
  async (config) => {
    // Try to get a fresh token from Firebase auth if user is logged in
    const { auth } = await import('@/firebase')
    const currentUser = auth.currentUser

    if (currentUser) {
      try {
        // Get fresh token (will use cached if not expired)
        const token = await currentUser.getIdToken()
        localStorage.setItem('authToken', token)
        config.headers.Authorization = `Bearer ${token}`
      } catch (error) {
        console.error('Failed to get auth token:', error)
        // Fallback to stored token
        const storedToken = localStorage.getItem('authToken')
        if (storedToken) {
          config.headers.Authorization = `Bearer ${storedToken}`
        }
      }
    } else {
      // No current user, try stored token
      const token = localStorage.getItem('authToken')
      if (token) {
        config.headers.Authorization = `Bearer ${token}`
      }
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor for error handling
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Handle unauthorized - redirect to login
      localStorage.removeItem('authToken')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export { apiClient as default }