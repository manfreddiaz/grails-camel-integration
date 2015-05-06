package grailscamelintegration

import grails.transaction.Transactional

@Transactional
class ReceivingService {

    def serviceMethod() {
        println 'Got my message'
    }
}
