data class Person(val phone: String, val name: String, val address: String) {

    override fun toString(): String {
        return "Phone => $phone, Name => $name, Address => $address"
    }
}

object PhoneDir {

    private val phoneMap = HashMap<String, Person>()
    private val tooManyMap = HashMap<String, Int>()

    private fun cleanAddress(address: String): String {
        var chars = arrayOf("/", "+", "?", "$", ";", "!", ":", "*", ",")
        var cleanedAddress = address

        // Remove "wrong" characters
        chars.forEach {
            cleanedAddress = cleanedAddress.replace(it, "")
        }

        // Trim the extra whitespaces
        cleanedAddress = cleanedAddress.split(" ").filter { it.trim().isNotEmpty() }.joinToString(separator = " ")

        // Very special case for whatever reason, must have space between the square brackets
        cleanedAddress = cleanedAddress.replace("[]", "[ ]")

        // Replace _ with " "
        cleanedAddress = cleanedAddress.replace("_", " ")

        return cleanedAddress
    }

    private fun processPhoneString(str: String): Person {
        // Find the phone
        val plusIndex = str.indexOf("+")
        val dashIndex = str.indexOf("-", plusIndex)
        val limit = plusIndex + dashIndex - plusIndex + 13
        val phoneNumber = str.substring(plusIndex + 1, limit)

        // Find the name
        val openBracketIndex = str.indexOf("<")
        val closeBracketIndex = str.indexOf(">", openBracketIndex)
        val name = str.substring(openBracketIndex + 1, closeBracketIndex)

        // Find the address
        var address = str.replace("+$phoneNumber", "").replace("<$name>", "")
        address = this.cleanAddress(address)

        return Person(phoneNumber, name, address)
    }

    private fun processPhoneMap(dir: String) {
        dir.split("\n").filter { it.trim().isNotEmpty() }.forEach {
            val person = this.processPhoneString(it)

            if (phoneMap.containsKey(person.phone)) {
                tooManyMap[person.phone] = 1
                phoneMap.remove(person.phone)
            } else {
                phoneMap[person.phone] = person
            }
        }
    }

    fun phone(strng: String, num: String): String {
        if (phoneMap.isEmpty()) {
            this.processPhoneMap(strng)
        }

        if (tooManyMap.containsKey(num)) {
            return "Error => Too many people: $num"
        } else if (!phoneMap.containsKey(num)) {
            return "Error => Not found: $num"
        }

        return phoneMap[num].toString()
    }
}

fun main() {
    val dr = ("/+1-541-754-3010 156 Alphand_St. <J Steeve>\n 133, Green, Rd. <E Kustur> NY-56423 ;+1-541-914-3010\n"
            + "<Anastasia> +48-421-674-8974 Via Quirinal Roma\n <P Salinger> Main Street, +1-098-512-2222, Denver\n"
            + "<Q Salinge> Main Street, +1-098-512-2222, Denve\n" + "<R Salinge> Main Street, +1-098-512-2222, Denve\n"
            + "<C Powel> *+19-421-674-8974 Chateau des Fosses Strasbourg F-68000\n <Bernard Deltheil> +1-498-512-2222; Mount Av.  Eldorado\n")

    println(PhoneDir.phone(dr, "48-421-674-8974"))
}