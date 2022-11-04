import axios from 'axios';

export const AxiosInstance = axios.create({
  headers: {
    'Bypass-Tunnel-Reminder': 1,
  },
});
