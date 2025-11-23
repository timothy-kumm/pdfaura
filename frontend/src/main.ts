import { createApp } from "vue";
import { createPinia } from "pinia";
import App from "./App.vue";
import { createI18n } from "vue-i18n";
import router from "./router";
import "./assets/index.css";
import englishMessages from "./i18n/englisch.json";
import germanMessages from "./i18n/german.json";

// Get stored language preference or default to 'en'
const storedLanguage = localStorage.getItem("language") || "en";

const i18n = createI18n({
  legacy: false,
  locale: storedLanguage,
  fallbackLocale: "en",
  messages: {
    en: englishMessages,
    de: germanMessages,
  },
});

const app = createApp(App);
const pinia = createPinia();

app.use(i18n);
app.use(pinia);
app.use(router);

app.mount("#app");
