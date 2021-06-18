package com.codechef.ffds

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.io.Serializable

data class User(
    val name: String,
    val email: String,
    val bio: String
)

data class Chat(
    val email: String,
    val with: String,
    val name: String,
    val msg: String,
    val time: String
)

@Entity
data class Profile(
    @PrimaryKey(autoGenerate = false) val primaryKey: Int = 0,
    val token: String = "",
    val verified: Boolean = false,
    val branch: String = "",
    val gender: String = "",
    val bio: String = "",
    val year: String = "",
    @TypeConverters(DataConverter::class) val expectations: List<String> = emptyList(),
    @TypeConverters(DataConverter::class) val slot: List<String> = emptyList(),
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val imagePath: String = "",
    val userImage: String = "",
    @TypeConverters(DataConverter::class) val chat: List<String> = emptyList(),
) : Serializable

data class ProfileResponse(
    val message: String,
    val user: Profile
)

data class Token(
    val message: String,
    val token: String
)

data class Feed(
    val message: String,
    val payload: ArrayList<Profile>
)

data class Messages(
    val lastMessage: String,
    val profileImage: Int,
    val name: String
)

class Slots {

    fun getSlots(): ArrayList<ArrayList<HashMap<String, Boolean>>> {

        val tableMap = ArrayList<ArrayList<HashMap<String, Boolean>>>()
        var itemArray = ArrayList<HashMap<String, Boolean>>()

        itemArray.add(map("A1/L1", false))
        itemArray.add(map("F1/L2", false))
        itemArray.add(map("D1/L3", false))
        itemArray.add(map("TB1/L4", false))
        itemArray.add(map("TG1/L5", false))
        itemArray.add(map("L6", false))
        itemArray.add(map("LUNCH", false))
        itemArray.add(map("A2/L31", false))
        itemArray.add(map("F2/L32", false))
        itemArray.add(map("D2/L33", false))
        itemArray.add(map("TB2/L34", false))
        itemArray.add(map("TG2/L35", false))
        itemArray.add(map("L36", false))
        itemArray.add(map("V3", false))
        tableMap.add(ArrayList(itemArray))
        itemArray = ArrayList()

        itemArray.add(map("B1/L7", false))
        itemArray.add(map("G1/L8", false))
        itemArray.add(map("E1/L9", false))
        itemArray.add(map("TC1/L10", false))
        itemArray.add(map("TAA1/L11", false))
        itemArray.add(map("L12", false))
        itemArray.add(map("LUNCH", false))
        itemArray.add(map("B2/L37", false))
        itemArray.add(map("G2/L38", false))
        itemArray.add(map("E2/L39", false))
        itemArray.add(map("TC2/L40", false))
        itemArray.add(map("TAA2/L41", false))
        itemArray.add(map("L42", false))
        itemArray.add(map("V4", false))
        tableMap.add(ArrayList(itemArray))
        itemArray = ArrayList()

        itemArray.add(map("C1/L13", false))
        itemArray.add(map("A1/L14", false))
        itemArray.add(map("F1/L15", false))
        itemArray.add(map("V1/L16", false))
        itemArray.add(map("V2/L17", false))
        itemArray.add(map("L18", false))
        itemArray.add(map("LUNCH", false))
        itemArray.add(map("C2/L43", false))
        itemArray.add(map("A2/L44", false))
        itemArray.add(map("F2/L45", false))
        itemArray.add(map("TD2/L46", false))
        itemArray.add(map("TBB2/L47", false))
        itemArray.add(map("L48", false))
        itemArray.add(map("V5", false))
        tableMap.add(ArrayList(itemArray))
        itemArray = ArrayList()

        itemArray.add(map("D1/L19", false))
        itemArray.add(map("B1/L20", false))
        itemArray.add(map("G1/L21", true))
        itemArray.add(map("TE1/L22", false))
        itemArray.add(map("TCC1/L23", false))
        itemArray.add(map("L24", false))
        itemArray.add(map("LUNCH", false))
        itemArray.add(map("D2/L49", false))
        itemArray.add(map("B2/L50", false))
        itemArray.add(map("G2/L51", false))
        itemArray.add(map("TE2/L52", false))
        itemArray.add(map("TCC2/L53", false))
        itemArray.add(map("L54", false))
        itemArray.add(map("V6", false))
        tableMap.add(ArrayList(itemArray))
        itemArray = ArrayList()

        itemArray.add(map("E1/L25", false))
        itemArray.add(map("C1/L26", false))
        itemArray.add(map("TA1/L27", false))
        itemArray.add(map("TF1/L28", false))
        itemArray.add(map("TD1/L29", false))
        itemArray.add(map("L30", false))
        itemArray.add(map("LUNCH", false))
        itemArray.add(map("E2/L55", false))
        itemArray.add(map("C2/L56", false))
        itemArray.add(map("TA2/L57", false))
        itemArray.add(map("TF2/L58", false))
        itemArray.add(map("TDD2/L59", false))
        itemArray.add(map("L60", false))
        itemArray.add(map("V7", false))
        tableMap.add(ArrayList(itemArray))
        itemArray = ArrayList()

        itemArray.add(map("V8/L71", false))
        itemArray.add(map("X11/L72", false))
        itemArray.add(map("X12/L73", false))
        itemArray.add(map("Y11/L74", false))
        itemArray.add(map("Y12/L75", false))
        itemArray.add(map("L88", false))
        itemArray.add(map("LUNCH", false))
        itemArray.add(map("X21/L77", false))
        itemArray.add(map("Z21/L78", false))
        itemArray.add(map("Y21/L79", false))
        itemArray.add(map("W21/L80", false))
        itemArray.add(map("W22/L81", false))
        itemArray.add(map("L82", false))
        itemArray.add(map("V9", false))
        tableMap.add(ArrayList(itemArray))

        itemArray.add(map("V10/L83", false))
        itemArray.add(map("Y11/L84", false))
        itemArray.add(map("Y12/L85", false))
        itemArray.add(map("X11/L86", false))
        itemArray.add(map("X12/L87", false))
        itemArray.add(map("L88", false))
        itemArray.add(map("LUNCH", false))
        itemArray.add(map("Y21/L89", false))
        itemArray.add(map("Z21/L90", false))
        itemArray.add(map("X21/L91", false))
        itemArray.add(map("W21/L92", false))
        itemArray.add(map("W22/L93", false))
        itemArray.add(map("L94", false))
        itemArray.add(map("V11", false))
        tableMap.add(ArrayList(itemArray))

        return tableMap
    }

    private fun map(key: String, value: Boolean): HashMap<String, Boolean> {
        val hashMap = HashMap<String, Boolean>()
        hashMap[key] = value
        return hashMap
    }
}
