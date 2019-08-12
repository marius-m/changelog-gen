package lt.markmerkk.changelog_gen.credits

import lt.markmerkk.changelog_gen.credits.entities.Credit

interface CreditsContract {
    interface View {
        fun renderCreditEntries(entries: List<String>)
        fun showCreditDetails(entry: Credit)
        fun showNoSuchCredits()
    }
    interface Presenter {
        fun onAttach(view: View)
        fun onDetach()
    }
}