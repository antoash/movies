import axios from "axios";

export default axios.create({
    baseURL: 'http://localhost:8080/',
    headers: {"ngrok-skil-browser-warning": "true"}
});