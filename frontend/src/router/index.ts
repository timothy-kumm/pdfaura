import { createRouter, createWebHistory } from "vue-router";
import Home from "../views/Home.vue";
import NotFound from "../views/NotFound.vue";
import Settings from "@/views/Settings/Settings.vue";
import PdfEditor from "@/views/PdfEditor.vue";

declare module "vue-router" {
  interface RouteMeta {
    requiresAuth?: boolean;
    guestOnly?: boolean;
  }
}

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: "/",
      name: "home",
      component: Home,
    },
    {
      path: "/settings",
      name: "settings",
      component: Settings,
      meta: { requiresAuth: true },
    },
    {
      path: "/:pathMatch(.*)*",
      name: "not-found",
      component: NotFound,
    },
    {
      path: "/pdf-editor",
      name: "pdf-editor",
      component: PdfEditor,
    },
  ],
  scrollBehavior() {
    return { top: 0 };
  },
});

export default router;
