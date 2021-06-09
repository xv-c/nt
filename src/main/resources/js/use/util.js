export default {
    truncate: function (text, length) {
        let newLength = -1
        let toAdd = ''
        if (text.length > length){
            newLength = length - 3
            toAdd = '...'
        } else {
            newLength = text.length
            toAdd = ''
        }
        return text.substring(0, newLength) + toAdd
    },
}