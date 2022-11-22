import axios from 'axios';

export const AxiosInstance = axios.create({
  headers: {
    'Bypass-Tunnel-Reminder': 1,
  },
});

export const googleOAuthClientId = '937013173995-cmhpl3s97umb1njiseqtk3c049guefi5.apps.googleusercontent.com';
