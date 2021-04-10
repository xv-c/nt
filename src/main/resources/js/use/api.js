import axios from "axios"


export default {
    async post(url, data) {
        try {
            let resp = await axios.post(url, data)
            return resp.data
        } catch (e) {
            return e.response.data
        }
    }
}