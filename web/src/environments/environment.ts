// Se usa en desarrollo local (npm start / ng serve).
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',
  // Esta configuracion de Firebase NO es secreta (es normal que vaya en el
  // codigo del frontend); lo que si es secreto es la clave de servicio que
  // usa el backend, y esa nunca va aqui.
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
