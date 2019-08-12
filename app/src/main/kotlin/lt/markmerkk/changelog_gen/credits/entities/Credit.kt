package lt.markmerkk.changelog_gen.credits.entities

data class Credit(
    val title: String,
    val description: String,
    val link: String,
    val license: String,
    val licenseLink: String,
    val licenseFull: String
)