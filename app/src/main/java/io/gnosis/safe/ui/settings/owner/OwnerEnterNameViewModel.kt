package io.gnosis.safe.ui.settings.owner

import io.gnosis.data.repositories.CredentialsRepository
import io.gnosis.safe.Tracker
import io.gnosis.safe.notifications.NotificationRepository
import io.gnosis.safe.ui.base.AppDispatchers
import io.gnosis.safe.ui.base.BaseStateViewModel
import io.gnosis.safe.ui.settings.app.SettingsHandler
import pm.gnosis.model.Solidity
import java.math.BigInteger
import javax.inject.Inject

class OwnerEnterNameViewModel
@Inject constructor(
    private val credentialsRepository: CredentialsRepository,
    private val notificationRepository: NotificationRepository,
    private val settingsHandler: SettingsHandler,
    private val tracker: Tracker,
    appDispatchers: AppDispatchers
) : BaseStateViewModel<OwnerEnterNameState>(appDispatchers) {

    override fun initialState() = OwnerEnterNameState(ViewAction.None)

    fun importOwner(address: Solidity.Address, name: String, key: BigInteger, fromSeedPhrase: Boolean) {
        safeLaunch {
            credentialsRepository.saveOwner(address, key, name)
            settingsHandler.showOwnerBanner = false
            settingsHandler.showOwnerScreen = false
            tracker.logKeyImported(fromSeedPhrase)
            tracker.setNumKeysImported(1)
            notificationRepository.registerOwner(key)

            updateState {
                OwnerEnterNameState(
                    if (settingsHandler.usePasscode) {
                        ViewAction.CloseScreen
                    } else {
                        ViewAction.NavigateTo(OwnerEnterNameFragmentDirections.actionOwnerEnterNameFragmentToCreatePasscodeFragment(true))
                    }
                )
            }
        }
    }
}

data class OwnerEnterNameState(
    override var viewAction: BaseStateViewModel.ViewAction?
) : BaseStateViewModel.State
