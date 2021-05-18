import axios from "axios"

export default {
    async delete(url) {
        try {
            let resp = await axios.delete(url)
            return resp.data
        } catch (e) {
            return e.response.data
        }
    },
    async post(url, data) {
        try {
            let resp = await axios.post(url, data)
            return resp.data
        } catch (e) {
            return e.response.data
        }
    },
    async get(url) {
        try {
            let resp = await axios.get(url)
            return resp.data
        } catch (e) {
            return e.response.data
        }
    }
}