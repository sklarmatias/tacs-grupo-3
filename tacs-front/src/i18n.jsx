import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import LanguageDetector from 'i18next-browser-languagedetector';
import HttpBackend from 'i18next-http-backend';


i18n
    .use(HttpBackend)
    .use(LanguageDetector)
    .use(initReactI18next)
    .init({
        debug: true,
        fallbackLng: 'es',
        backend: {
            loadPath: 'src/locales/{{lng}}/translation.json'
        },
        detection: {
            // Order and from where user language should be detected
            order: ['querystring', 'navigator', 'htmlTag'],
            // Cache user language on
            //caches: ['localStorage', 'cookie'],
        },
    });

export default i18n;