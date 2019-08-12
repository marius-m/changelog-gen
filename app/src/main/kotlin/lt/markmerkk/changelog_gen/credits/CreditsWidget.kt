package lt.markmerkk.changelog_gen.credits

import com.google.common.base.Strings
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler
import io.reactivex.schedulers.Schedulers
import javafx.collections.ListChangeListener
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.control.SelectionMode
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import lt.markmerkk.changelog_gen.credits.entities.Credit
import lt.markmerkk.changelog_gen.credits.repository.CreditsRepository
import org.slf4j.LoggerFactory
import tornadofx.*

class CreditsWidget(): View(), CreditsContract.View {

    private val creditsRepository: CreditsRepository by di()
    private val strings: Strings by di()

    private lateinit var viewTextTitle: Label
    private lateinit var viewTextDescription: Text
    private lateinit var viewTextLink: TextField
    private lateinit var viewTextLicense: Label
    private lateinit var viewTextLicenseLink: TextField
    private lateinit var viewTextLicenseFull: TextArea
    private lateinit var viewContainerDetail: VBox

    val presenter = CreditsPresenter(
        creditsRepository = creditsRepository,
        ioScheduler = Schedulers.computation(),
        uiScheduler = JavaFxScheduler.platform()
    )

    private val creditItems = listOf<String>()
        .toObservable()

    override val root: Parent = borderpane {
        left {
            listview(creditItems) {
                selectionModel.selectionMode = SelectionMode.SINGLE
                selectionModel.selectedItems.addListener(ListChangeListener<String> {
                    presenter.findCreditDetails(selectedItem ?: "")
                })
            }
        }
        center {
            viewContainerDetail = vbox(spacing = 4) {
                style {
                    padding = box(4.px)
                }
                minWidth = 400.0
                minHeight = 500.0
                form {
                    fieldset("Credits") {
                        field("Details") {
                            viewTextTitle = label { addClass("h1") }
                        }
                        field("Link") {
                            viewTextLink = textfield {
                                addClass("h4")
                                isEditable = false
                            }
                        }
                        field("License link") {
                            viewTextLicenseLink = textfield {
                                addClass("h4")
                                isEditable = false
                            }
                        }
                        field("Description") {
                            viewTextDescription = text {
                                addClass("p")
                                wrappingWidth = 300.0
                            }
                        }
                        field("Full license") {
                            viewTextLicense = label { addClass("p") }
                        }
                    }
                }
                viewTextLicenseFull = textarea {
                    prefRowCount = 5
                    vgrow = Priority.ALWAYS
                    isEditable = false
                }
            }
        }
    }

    override fun onDock() {
        super.onDock()
        this.presenter.onAttach(this)
    }

    override fun onUndock() {
        super.onUndock()
        this.presenter.onDetach()
    }

    override fun renderCreditEntries(entries: List<String>) {
        creditItems.clear()
        creditItems.addAll(entries)
    }

    override fun showCreditDetails(entry: Credit) {
        viewContainerDetail.isVisible = true
        viewTextTitle.text = entry.title
        viewTextDescription.text = entry.description
        viewTextLink.text = entry.link
        viewTextLicense.text = entry.license
        viewTextLicenseLink.text = entry.licenseLink
        viewTextLicenseFull.text = entry.licenseFull
    }

    override fun showNoSuchCredits() {
        viewContainerDetail.isVisible = false
    }

    companion object {
        val logger = LoggerFactory.getLogger(CreditsWidget::class.java)!!
    }

}