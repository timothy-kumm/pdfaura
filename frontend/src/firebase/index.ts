import { initializeApp } from 'firebase/app';
import { getAuth } from 'firebase/auth';

const firebaseConfig = {
  apiKey: "AIzaSyDiQMrZb-RYYNs95acR3mIIpG52Ze0RjdE",
  authDomain: "pdfaura.firebaseapp.com",
  projectId: "pdfaura",
  storageBucket: "pdfaura.firebasestorage.app",
  messagingSenderId: "568323337278",
  appId: "1:568323337278:web:c82bf43ada43212eb7b56f",
  measurementId: "G-JC6QF7V060"
};

const app = initializeApp(firebaseConfig);
const auth = getAuth(app);

export { auth, app };
