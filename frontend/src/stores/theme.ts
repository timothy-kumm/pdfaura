import { defineStore } from "pinia";

export const useThemeStore = defineStore("theme", {
  state: () => ({
    isDarkMode: false,
  }),

  getters: {
    currentTheme: (state) => (state.isDarkMode ? "dark" : "light"),
  },

  actions: {
    toggleTheme() {
      this.isDarkMode = !this.isDarkMode;
    },

    setTheme(isDark: boolean) {
      this.isDarkMode = isDark;
    },
  },
});
