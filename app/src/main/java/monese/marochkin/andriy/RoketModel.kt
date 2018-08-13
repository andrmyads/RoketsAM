package monese.marochkin.andriy

class RoketModel {
    var id: String? = null
    var name: String? = null
    var country: String? = null
    var description: String? = null

    var active: Boolean? = null

    var engcount: Int? = null


    constructor(id: String, name: String, country: String , active: Boolean,  engcount: Int, description: String) {
        this.id = id
        this.name = name
        this.country = country
        this.active = active
        this.country = country
        this.engcount = engcount
        this.description = description
    }
}