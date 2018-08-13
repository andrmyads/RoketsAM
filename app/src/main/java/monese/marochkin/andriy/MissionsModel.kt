package monese.marochkin.andriy

class MissionsModel {
    var picture: String? = null
    var name: String? = null
    var date: Long? = null
    var successful: Boolean? = null


    constructor(picture: String, name: String, date: Long , successful: Boolean) {
        this.picture = picture
        this.name = name
        this.date = date
        this.successful = successful
    }
}