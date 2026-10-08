// Se usa al compilar para producción (ng build). Backend real en Render (ya no
// depende de tu PC prendida con el túnel de ngrok). Si vuelves a renombrar el
// servicio en Render, actualiza esta URL con la nueva.
export const environment = {
  production: true,
  apiUrl: 'https://kutt-backend-94sz.onrender.com/api',
  firebase: {
    apiKey: "AIzaSyCwytMnRcF1On8e3G2Ku316ueuF-TEVcNw",
    authDomain: "kutt-8ba8f.firebaseapp.com",
    projectId: "kutt-8ba8f",
    storageBucket: "kutt-8ba8f.firebasestorage.app",
    messagingSenderId: "156305516020",
    appId: "1:156305516020:web:3fba469363df90b045c5a1",
    measurementId: "G-NSZXV63N79"
  },
};
