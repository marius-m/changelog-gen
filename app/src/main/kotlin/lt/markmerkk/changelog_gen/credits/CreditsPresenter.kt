package lt.markmerkk.changelog_gen.credits

import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import lt.markmerkk.changelog_gen.credits.repository.CreditsRepository
import org.slf4j.LoggerFactory

class CreditsPresenter(
    private val creditsRepository: CreditsRepository,
    private val ioScheduler: Scheduler,
    private val uiScheduler: Scheduler
): CreditsContract.Presenter {

    private var view: CreditsContract.View? = null
    private var entriesDisposable: Disposable? = null
    private var findEntryDisposable: Disposable? = null

    override fun onAttach(view: CreditsContract.View) {
        this.view = view
        entriesDisposable = creditsRepository.creditEntries()
            .subscribeOn(ioScheduler)
            .observeOn(uiScheduler)
            .subscribe({ entries ->
                if (entries.isNotEmpty()) {
                    val creditTitles = entries
                        .map { it.title }
                        .sorted()
                    this.view?.renderCreditEntries(creditTitles)
                    findCreditDetails(creditTitles.first())
                } else {
                    logger.warn("No license files loaded")
                }
            }, { error ->
                logger.warn("Error reading credit entries", error)
            })
    }

    override fun onDetach() {
        this.view = null
        entriesDisposable?.dispose()
        findEntryDisposable?.dispose()
    }

    fun findCreditDetails(creditTitle: String) {
        findEntryDisposable?.dispose()
        findEntryDisposable = creditsRepository.creditEntries()
            .subscribeOn(ioScheduler)
            .observeOn(uiScheduler)
            .subscribe({ entries ->
                val foundEntries = entries
                    .filter { it.title == creditTitle }
                if (foundEntries.isNotEmpty()) {
                    view?.showCreditDetails(foundEntries.first())
                } else {
                    view?.showNoSuchCredits()
                }
            }, { error ->
                logger.warn("Error trying to look for entry", error)
                view?.showNoSuchCredits()
            })
    }

    companion object {
        val logger = LoggerFactory.getLogger(CreditsPresenter::class.java)!!
    }

}