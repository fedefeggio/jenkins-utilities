/* groovylint-disable */
package fedefeggio;

class Utilities implements Serializable {

    def steps
    Utilities(steps) { this.steps = steps }

    def sendTelegramMessage(String telegramToken, String chatId, String message){
        steps.withEnv(['TELEGRAM_TOKEN=' + telegramToken, 'CHAT_ID=' + chatId, 'MESSAGE=' + message]) {
            steps.sh '''
                curl -s -X POST https://api.telegram.org/bot${TELEGRAM_TOKEN}/sendMessage \
                -d chat_id=${CHAT_ID} -d text="${MESSAGE}"
            '''
        }
    }

    def sudoSh(String sudoPwd, String command) {
        steps.withEnv(['SUDO_PWD=' + sudoPwd, 'COMMAND=' + command]) {
            steps.sh '''
                echo \"${SUDO_PWD}\" | sudo -S ${COMMAND}
            '''
        }
    }
}
