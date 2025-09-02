// src/environments/environment.ts
export const environment = {
  production: false,
  //RESOURSE_URL: 'http://localhost:8280',
  RESOURSE_URL: 'http://localhost:8765/rs-main',
  MS_NOTIFICATION_URL: 'http://localhost:8765/ms-notification',
  KC_CLIENT_ID:'logoped-client',
  KC_URI:'http://localhost:8180/realms/logoped-realm/protocol/openid-connect',
  BFF_URI:"http://localhost:8380",
  REDIRECT_URL: 'http://localhost:4200/login'
};
