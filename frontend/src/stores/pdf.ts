import { defineStore } from 'pinia';

export const usePdfStore = defineStore('pdf', {
  state: () => ({
    file: null as File | null,
  }),
  actions: {
    setFile(file: File) {
      this.file = file;
    },
  },
});
