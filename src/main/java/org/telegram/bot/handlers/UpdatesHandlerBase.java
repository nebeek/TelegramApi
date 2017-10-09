package org.telegram.bot.handlers;

import org.jetbrains.annotations.NotNull;
import org.telegram.api.chat.TLAbsChat;
import org.telegram.api.message.TLAbsMessage;
import org.telegram.api.message.TLMessage;
import org.telegram.api.message.TLMessageService;
import org.telegram.api.notify.peer.TLAbsNotifyPeer;
import org.telegram.api.notify.peer.TLNotifyPeer;
import org.telegram.api.peer.TLAbsPeer;
import org.telegram.api.peer.TLPeerUser;
import org.telegram.api.update.*;
import org.telegram.api.update.encrypted.TLUpdateEncryptedChatTyping;
import org.telegram.api.update.encrypted.TLUpdateEncryptedMessagesRead;
import org.telegram.api.update.encrypted.TLUpdateEncryption;
import org.telegram.api.update.encrypted.TLUpdateNewEncryptedMessage;
import org.telegram.api.updates.TLUpdateShortChatMessage;
import org.telegram.api.updates.TLUpdateShortMessage;
import org.telegram.api.updates.TLUpdateShortSentMessage;
import org.telegram.api.updates.TLUpdatesState;
import org.telegram.api.updates.difference.TLAbsDifference;
import org.telegram.api.user.TLAbsUser;
import org.telegram.bot.handlers.interfaces.IDifferencesHandler;
import org.telegram.bot.handlers.interfaces.IUpdatesHandler;
import org.telegram.bot.kernel.IKernelComm;
import org.telegram.bot.kernel.UpdateWrapper;
import org.telegram.bot.kernel.database.DatabaseManager;
import org.telegram.bot.kernel.differenceparameters.IDifferenceParametersService;
import org.telegram.bot.services.BotLogger;
import org.telegram.bot.structure.Chat;
import org.telegram.tl.TLObject;

import java.util.List;

/**
 * @author Ruben Bermudez
 * @version 1.0
 * @brief Base plugins of the updates handler, should be provided to MainHandler.
 * Has some final method, but also provide option to add custom behaviour
 * @date 22 of March of 2016
 */
@SuppressWarnings("OverlyComplexClass")
public abstract class UpdatesHandlerBase implements IUpdatesHandler {
    private static final String LOGTAG = "UPDATESHANDLERBASE";

    private final IDifferenceParametersService differenceParametersService;
    private final IDifferencesHandler differencesHandler;
    private final DatabaseManager databaseManager;

    protected UpdatesHandlerBase(IKernelComm kernelComm, IDifferenceParametersService differenceParametersService, DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        this.differenceParametersService = differenceParametersService;
        differencesHandler = new DifferencesHandler(kernelComm, differenceParametersService, this);
    }

    @Override
    public final void processUpdate(UpdateWrapper updateWrapper) {
        final TLObject update = updateWrapper.getUpdate();
        BotLogger.debug(LOGTAG, "Recived update: " + update.toString());
        if (update instanceof TLUpdateShortMessage) {
            onTLUpdateShortMessage((TLUpdateShortMessage) update, updateWrapper.isGettingDifferences());
        } else if (update instanceof TLUpdateShortChatMessage) {
            onTLUpdateShortChatMessage((TLUpdateShortChatMessage) update, updateWrapper.isGettingDifferences());
        } else if (update instanceof TLUpdateShortSentMessage) {
            onTLUpdateShortSentMessage((TLUpdateShortSentMessage) update);
        } else if (update instanceof TLUpdateNewMessage) {
            onTLUpdateNewMessage((TLUpdateNewMessage) update, updateWrapper.isGettingDifferences());
        } else if (update instanceof TLUpdateChatParticipants) {
            onTLUpdateChatParticipants((TLUpdateChatParticipants) update, updateWrapper.isGettingDifferences());
        } else if (update instanceof TLUpdateChannelNewMessage) {
            onTLUpdateChannelNewMessage((TLUpdateChannelNewMessage) update, updateWrapper.isGettingDifferences());
        } else if (update instanceof TLUpdateChannel) {
            onTLUpdateChannel((TLUpdateChannel) update, updateWrapper.isGettingDifferences());
        } else if (update instanceof TLUpdateBotInlineQuery) {
            onTLUpdateBotInlineQuery((TLUpdateBotInlineQuery) update, updateWrapper.isGettingDifferences());
        } else if (update instanceof TLUpdateBotInlineSend) {
            onTLUpdateBotInlineSend((TLUpdateBotInlineSend) update, updateWrapper.isGettingDifferences());
        } else if (update instanceof TLUpdateChannelMessageViews) {
            onTLUpdateChannelMessageViews((TLUpdateChannelMessageViews) update, updateWrapper.isGettingDifferences());
        } else if (update instanceof TLUpdateChannelPinnedMessage) {
            onTLUpdateChannelPinnedMessage((TLUpdateChannelPinnedMessage) update, updateWrapper.isGettingDifferences());
        } else if (update instanceof TLUpdateChannelTooLong) {
            onTLUpdateChannelTooLong((TLUpdateChannelTooLong) update, updateWrapper.isGettingDifferences());
        } else if (update instanceof TLUpdateChatAdmin) {
            onTLUpdateChatAdmin((TLUpdateChatAdmin) update, updateWrapper.isGettingDifferences());
        } else if (update instanceof TLUpdateChatParticipantAdd) {
            onTLUpdateChatParticipantAdd((TLUpdateChatParticipantAdd) update, updateWrapper.isGettingDifferences());
        } else if (update instanceof TLUpdateChatParticipantAdmin) {
            onTLUpdateChatParticipantAdmin((TLUpdateChatParticipantAdmin) update, updateWrapper.isGettingDifferences());
        } else if (update instanceof TLUpdateChatParticipantDelete) {
            onTLUpdateChatParticipantDelete((TLUpdateChatParticipantDelete) update, updateWrapper.isGettingDifferences());
        } else if (update instanceof TLUpdateChatUserTyping) {
            onTLUpdateChatUserTyping((TLUpdateChatUserTyping) update, updateWrapper.isGettingDifferences());
        } else if (update instanceof TLUpdateContactLink) {
            onTLUpdateContactLink((TLUpdateContactLink) update, updateWrapper.isGettingDifferences());
        } else if (update instanceof TLUpdateContactRegistered) {
            onTLUpdateContactRegistered((TLUpdateContactRegistered) update, updateWrapper.isGettingDifferences());
        } else if (update instanceof TLUpdateDcOptions) {
            onTLUpdateDcOptions((TLUpdateDcOptions) update);
        } else if (update instanceof TLUpdateDeleteChannelMessages) {
            onTLUpdateDeleteChannelMessages((TLUpdateDeleteChannelMessages) update, updateWrapper.isGettingDifferences());
        } else if (update instanceof TLUpdateDeleteMessages) {
            onTLUpdateDeleteMessages((TLUpdateDeleteMessages) update, updateWrapper.isGettingDifferences());
        } else if (update instanceof TLUpdateEditChannelMessage) {
            onTLUpdateEditChannelMessage((TLUpdateEditChannelMessage) update, updateWrapper.isGettingDifferences());
        } else if (update instanceof TLUpdateMessageId) {
            onTLUpdateMessageId((TLUpdateMessageId) update);
        } else if (update instanceof TLUpdateNewStickerSet) {
            onTLUpdateNewStickerSet((TLUpdateNewStickerSet) update);
        } else if (update instanceof TLUpdateNotifySettings) {
            onTLUpdateNotifySettings((TLUpdateNotifySettings) update, updateWrapper.isGettingDifferences());
        } else if (update instanceof TLUpdatePrivacy) {
            onTLUpdatePrivacy((TLUpdatePrivacy) update);
        } else if (update instanceof TLUpdateReadChannelInbox) {
            onTLUpdateReadChannelInbox((TLUpdateReadChannelInbox) update, updateWrapper.isGettingDifferences());
        } else if (update instanceof TLUpdateReadMessagesContents) {
            onTLUpdateReadMessagesContents((TLUpdateReadMessagesContents) update);
        } else if (update instanceof TLUpdateReadMessagesInbox) {
            onTLUpdateReadMessagesInbox((TLUpdateReadMessagesInbox) update, updateWrapper.isGettingDifferences());
        } else if (update instanceof TLUpdateReadMessagesOutbox) {
            onTLUpdateReadMessagesOutbox((TLUpdateReadMessagesOutbox) update, updateWrapper.isGettingDifferences());
        } else if (update instanceof TLUpdateSavedGifs) {
            onTLUpdateSavedGifs((TLUpdateSavedGifs) update);
        } else if (update instanceof TLUpdateServiceNotification) {
            onTLUpdateServiceNotification((TLUpdateServiceNotification) update);
        } else if (update instanceof TLUpdateStickerSets) {
            onTLUpdateStickerSets((TLUpdateStickerSets) update);
        } else if (update instanceof TLUpdateStickerSetsOrder) {
            onTLUpdateStickerSetsOrder((TLUpdateStickerSetsOrder) update);
        } else if (update instanceof TLUpdateUserBlocked) {
            onTLUpdateUserBlocked((TLUpdateUserBlocked) update, updateWrapper.isGettingDifferences());
        } else if (update instanceof TLUpdateUserName) {
            onTLUpdateUserName((TLUpdateUserName) update, updateWrapper.isGettingDifferences());
        } else if (update instanceof TLUpdateUserPhone) {
            onTLUpdateUserPhone((TLUpdateUserPhone) update, updateWrapper.isGettingDifferences());
        } else if (update instanceof TLUpdateUserPhoto) {
            onTLUpdateUserPhoto((TLUpdateUserPhoto) update, updateWrapper.isGettingDifferences());
        } else if (update instanceof TLUpdateUserStatus) {
            onTLUpdateUserStatus((TLUpdateUserStatus) update, updateWrapper.isGettingDifferences());
        } else if (update instanceof TLUpdateUserTyping) {
            onTLUpdateUserTyping((TLUpdateUserTyping) update, updateWrapper.isGettingDifferences());
        } else if (update instanceof TLUpdateWebPage) {
            onTLUpdateWebPage((TLUpdateWebPage) update);
        } else if (update instanceof TLFakeUpdate) {
            onTLFakeUpdate((TLFakeUpdate) update);
        } else if (update instanceof TLUpdateBotCallbackQuery) {
            onTLUpdateBotCallbackQuery((TLUpdateBotCallbackQuery) update, updateWrapper.isGettingDifferences());
        } else if (update instanceof TLUpdateEditMessage) {
            onTLUpdateEditMessage((TLUpdateEditMessage) update, updateWrapper.isGettingDifferences());
        } else if (update instanceof TLUpdateInlineBotCallbackQuery) {
            onTLUpdateInlineBotCallbackQuery((TLUpdateInlineBotCallbackQuery) update, updateWrapper.isGettingDifferences());
        } else if (update instanceof TLUpdateEncryption) {
            onTLUpdateEncryption((TLUpdateEncryption) update);
        } else if (update instanceof TLUpdateEncryptedChatTyping) {
            onTLUpdateEncryptedChatTyping((TLUpdateEncryptedChatTyping) update);
        } else if (update instanceof TLUpdateEncryptedMessagesRead) {
            onTLUpdateEncryptedMessagesRead((TLUpdateEncryptedMessagesRead) update);
        } else if (update instanceof TLUpdateNewEncryptedMessage) {
            onTLUpdateNewEncryptedMessage((TLUpdateNewEncryptedMessage) update);
        } else if (update instanceof TLUpdateConfig) {
            onTLUpdateConfig((TLUpdateConfig) update);
        } else if (update instanceof TLUpdateDraftMessage) {
            onTLUpdateDraftMessage((TLUpdateDraftMessage) update, updateWrapper.isGettingDifferences());
        } else if (update instanceof TLUpdatePtsChanged) {
            onTLUpdatePtsChanged((TLUpdatePtsChanged) update);
        } else if (update instanceof TLUpdateReadChannelOutbox) {
            onTLUpdateReadChannelOutbox((TLUpdateReadChannelOutbox) update, updateWrapper.isGettingDifferences());
        } else if (update instanceof TLUpdateReadFeaturedStickers) {
            onTLUpdateReadFeaturedStickers((TLUpdateReadFeaturedStickers) update);
        } else if (update instanceof TLUpdateRecentStickers) {
            onTLUpdateRecentStickers((TLUpdateRecentStickers) update);
        } else if (update instanceof TLUpdateChannelWebPage) {
            onTLUpdateChannelWebPage((TLUpdateChannelWebPage) update, updateWrapper.isGettingDifferences());
        } else if (update instanceof TLUpdatePhoneCall) {
            onTLUpdatePhoneCall((TLUpdatePhoneCall) update);
        } else if (update instanceof TLUpdateDialogPinned) {
            onTLUpdateDialogPinned((TLUpdateDialogPinned) update);
        } else if (update instanceof TLUpdatePinnedDialogs) {
            onTLUpdatePinnedDialogs((TLUpdatePinnedDialogs) update);
        } else if (update instanceof TLUpdateBotWebhookJSON) {
            onTLUpdateBotWebhookJSON((TLUpdateBotWebhookJSON) update);
        } else if (update instanceof TLUpdateBotWebhookJSONQuery) {
            onTLUpdateBotWebhookJSONQuery((TLUpdateBotWebhookJSONQuery) update);
        } else if (update instanceof TLUpdateBotShippingQuery) {
            onTLUpdateBotShippingQuery((TLUpdateBotShippingQuery) update, updateWrapper.isGettingDifferences());
        } else if (update instanceof TLUpdateBotPrecheckoutQuery) {
            onTLUpdateBotPrecheckoutQuery((TLUpdateBotPrecheckoutQuery) update, updateWrapper.isGettingDifferences());
        } else {
            BotLogger.debug(LOGTAG, "Unsupported TLAbsUpdate: " + update.toString());
        }
        if (updateWrapper.isUpdatePts()){
            updatePts(updateWrapper);
        }
    }

    @Override
    public final boolean checkSeq(int seq, int seqStart, int date) {
        boolean canHandle = false;

        seqStart = (seqStart == 0) ? seq : seqStart;
        if (seqStart == (differenceParametersService.getSeq(0) + 1)) {
            canHandle = true;
        }

        return canHandle;
    }

    @Override
    public final void getDifferences() {
        differencesHandler.getDifferences();
    }

    private void updatePts(UpdateWrapper updateWrapper) {
        differenceParametersService.setNewUpdateParams(updateWrapper.getChannelId(), updateWrapper.getPts(),
                updateWrapper.getSeq(), updateWrapper.getDate());
    }

    private void onTLUpdateShortMessage(TLUpdateShortMessage update, boolean gettingDifferences) {
    	onTLUpdateShortMessageCustom(update);
    }

    private void onTLUpdateShortChatMessage(TLUpdateShortChatMessage update, boolean gettingDifferences) {
    	onTLUpdateShortChatMessageCustom(update);
    }

    private void onTLUpdateShortSentMessage(TLUpdateShortSentMessage update) {
        onTLUpdateShortSentMessageCustom(update);
    }

    private void onTLUpdateDialogPinned(TLUpdateDialogPinned update) {
        onTLUpdateDialogPinnedCustom(update);
    }

    private void onTLUpdatePinnedDialogs(TLUpdatePinnedDialogs update) {
        onTLUpdatePinnedDialogsCustom(update);
    }

    private void onTLUpdateBotWebhookJSON(TLUpdateBotWebhookJSON update) {
        onTLUpdateBotWebhookJSONCustom(update);
    }

    private void onTLUpdateBotWebhookJSONQuery(TLUpdateBotWebhookJSONQuery update) {
        onTLUpdateBotWebhookJSONQueryCustom(update);
    }

    private void onTLUpdatePhoneCall(TLUpdatePhoneCall update) {
        onTLUpdatePhoneCallCustom(update);
    }

    private void onTLUpdateChannelWebPage(TLUpdateChannelWebPage update, boolean gettingDifferences) {
    	onTLUpdateChannelWebPageCustom(update);
    }

    private void onTLUpdateChatParticipants(TLUpdateChatParticipants update, boolean gettingDifferences) {
    	onTLUpdateChatParticipantsCustom(update);
    }

    private void onTLUpdateNewMessage(TLUpdateNewMessage update, boolean gettingDifferences) {
    	onTLUpdateNewMessageCustom(update);
    }

    private void onTLUpdateBotShippingQuery(TLUpdateBotShippingQuery update, boolean gettingDifferences) {
    	onTLUpdateBotShippingQueryCustom(update);
    }

    private void onTLUpdateBotPrecheckoutQuery(TLUpdateBotPrecheckoutQuery update, boolean gettingDifferences) {
    	onTLUpdateBotPrecheckoutQueryCustom(update);
    }

    private void onTLUpdateChannelNewMessage(TLUpdateChannelNewMessage update, boolean gettingDifferences) {
    	onTLUpdateChannelNewMessageCustom(update);
    }

    private void onTLUpdateChannel(TLUpdateChannel update, boolean gettingDifferences) {
    	onTLUpdateChannelCustom(update);
    }

    private void onTLUpdateBotInlineQuery(TLUpdateBotInlineQuery update, boolean gettingDifferences) {
    	onTLUpdateBotInlineQueryCustom(update);
    }

    private void onTLUpdateBotInlineSend(TLUpdateBotInlineSend update, boolean gettingDifferences) {
    	onTLUpdateBotInlineSendCustom(update);
    }

    private void onTLUpdateChannelMessageViews(TLUpdateChannelMessageViews update, boolean gettingDifferences) {
    	onTLUpdateChannelMessageViewsCustom(update);
    }

    private void onTLUpdateChannelPinnedMessage(TLUpdateChannelPinnedMessage update, boolean gettingDifferences) {
    	onTLUpdateChannelPinnedMessageCustom(update);
    }

    private void onTLUpdateChannelTooLong(TLUpdateChannelTooLong update, boolean gettingDifferences) {
        onTLUpdateChannelTooLongCustom(update);
    }

    private void onTLUpdateChatAdmin(TLUpdateChatAdmin update, boolean gettingDifferences) {
    	onTLUpdateChatAdminCustom(update);
    }

    private void onTLUpdateChatParticipantAdd(TLUpdateChatParticipantAdd update, boolean gettingDifferences) {
    	onTLUpdateChatParticipantAddCustom(update);
    }

    private void onTLUpdateChatParticipantAdmin(TLUpdateChatParticipantAdmin update, boolean gettingDifferences) {
    	onTLUpdateChatParticipantAdminCustom(update);
    }

    private void onTLUpdateChatParticipantDelete(TLUpdateChatParticipantDelete update, boolean gettingDifferences) {
    	onTLUpdateChatParticipantDeleteCustom(update);
    }

    private void onTLUpdateChatUserTyping(TLUpdateChatUserTyping update, boolean gettingDifferences) {
    	onTLUpdateChatUserTypingCustom(update);
    }

    private void onTLUpdateContactLink(TLUpdateContactLink update, boolean gettingDifferences) {
    	onTLUpdateContactLinkCustom(update);
    }

    private void onTLUpdateContactRegistered(TLUpdateContactRegistered update, boolean gettingDifferences) {
    	onTLUpdateContactRegisteredCustom(update);
    }

    private void onTLUpdateDcOptions(TLUpdateDcOptions update) {
        onTLUpdateDcOptionsCustom(update);
    }

    private void onTLUpdateDeleteChannelMessages(TLUpdateDeleteChannelMessages update, boolean gettingDifferences) {
    	onTLUpdateDeleteChannelMessagesCustom(update);
    }

    private void onTLUpdateDeleteMessages(TLUpdateDeleteMessages update, boolean gettingDifferences) {
        onTLUpdateDeleteMessagesCustom(update);
    }

    private void onTLUpdateEditChannelMessage(TLUpdateEditChannelMessage update, boolean gettingDifferences) {
    	onTLUpdateEditChannelMessageCustom(update);
    }

    private void onTLUpdateMessageId(TLUpdateMessageId update) {
        onTLUpdateMessageIdCustom(update);
    }

    private void onTLUpdateNewStickerSet(TLUpdateNewStickerSet update) {
        onTLUpdateNewStickerSetCustom(update);
    }

    private void onTLUpdateNotifySettings(TLUpdateNotifySettings update, boolean gettingDifferences) {
    	onTLUpdateNotifySettingsCustom(update);
    }

    private void onTLUpdatePrivacy(TLUpdatePrivacy update) {
        onTLUpdatePrivacyCustom(update);
    }

    private void onTLUpdateReadChannelInbox(TLUpdateReadChannelInbox update, boolean gettingDifferences) {
    	onTLUpdateReadChannelInboxCustom(update);
    }

    private void onTLUpdateReadMessagesContents(TLUpdateReadMessagesContents update) {
        onTLUpdateReadMessagesContentsCustom(update);
    }

    private void onTLUpdateReadMessagesInbox(TLUpdateReadMessagesInbox update, boolean gettingDifferences) {
    	onTLUpdateReadMessagesInboxCustom(update);
    }

    private void onTLUpdateReadMessagesOutbox(TLUpdateReadMessagesOutbox update, boolean gettingDifferences) {
    	onTLUpdateReadMessagesOutboxCustom(update);
    }

    private void onTLUpdateSavedGifs(TLUpdateSavedGifs update) {
        onTLUpdateSavedGifsCustom(update);
    }

    private void onTLUpdateServiceNotification(TLUpdateServiceNotification update) {
        onTLUpdateServiceNotificationCustom(update);
    }

    private void onTLUpdateStickerSets(TLUpdateStickerSets update) {
        onTLUpdateStickerSetsCustom(update);
    }

    private void onTLUpdateStickerSetsOrder(TLUpdateStickerSetsOrder update) {
        onTLUpdateStickerSetsOrderCustom(update);
    }

    private void onTLUpdateUserBlocked(TLUpdateUserBlocked update, boolean gettingDifferences) {
    	onTLUpdateUserBlockedCustom(update);
    }

    private void onTLUpdateUserName(TLUpdateUserName update, boolean gettingDifferences) {
    	onTLUpdateUserNameCustom(update);
    }

    private void onTLUpdateUserPhone(TLUpdateUserPhone update, boolean gettingDifferences) {
    	onTLUpdateUserPhoneCustom(update);
    }

    private void onTLUpdateUserPhoto(TLUpdateUserPhoto update, boolean gettingDifferences) {
    	onTLUpdateUserPhotoCustom(update);
    }

    private void onTLUpdateUserStatus(TLUpdateUserStatus update, boolean gettingDifferences) {
    	onTLUpdateUserStatusCustom(update);
    }

    private void onTLUpdateUserTyping(TLUpdateUserTyping update, boolean gettingDifferences) {
    	onTLUpdateUserTypingCustom(update);
    }

    private void onTLUpdateWebPage(TLUpdateWebPage update) {
        onTLUpdateWebPageCustom(update);
    }

    private void onTLUpdateBotCallbackQuery(TLUpdateBotCallbackQuery update, boolean gettingDifferences) {
    	onTLUpdateBotCallbackQueryCustom(update);
    }

    private void onTLUpdateEditMessage(TLUpdateEditMessage update, boolean gettingDifferences){
    	onTLUpdateEditMessageCustom(update);
    }

    private void onTLUpdateInlineBotCallbackQuery(TLUpdateInlineBotCallbackQuery update, boolean gettingDifferences){
    	onTLUpdateInlineBotCallbackQueryCustom(update);
    }

    private void onTLUpdateEncryption(TLUpdateEncryption update){
        onTLUpdateEncryptionCustom(update);
    }

    private void onTLUpdateEncryptedChatTyping(TLUpdateEncryptedChatTyping update) {
        onTLUpdateEncryptedChatTypingCustom(update);
    }

    private void onTLUpdateEncryptedMessagesRead(TLUpdateEncryptedMessagesRead update) {
        onTLUpdateEncryptedMessagesReadCustom(update);
    }

    private void onTLUpdateNewEncryptedMessage(TLUpdateNewEncryptedMessage update) {
        onTLUpdateNewEncryptedMessageCustom(update);
    }

    private void onTLUpdateConfig(TLUpdateConfig update) {
        onTLUpdateConfigCustom(update);
    }

    private void onTLUpdateDraftMessage(TLUpdateDraftMessage update, boolean gettingDifferences) {
    	onTLUpdateDraftMessageCustom(update);
    }

    private void onTLUpdatePtsChanged(TLUpdatePtsChanged update) {
        onTLUpdatePtsChangedCustom(update);
    }

    private void onTLUpdateReadChannelOutbox(TLUpdateReadChannelOutbox update, boolean gettingDifferences) {
    	onTLUpdateReadChannelOutboxCustom(update);
    }

    private void onTLUpdateReadFeaturedStickers(TLUpdateReadFeaturedStickers update) {
        onTLUpdateReadFeaturedStickersCustom(update);
    }

    private void onTLUpdateRecentStickers(TLUpdateRecentStickers update) {
        onTLUpdateRecentStickersCustom(update);
    }

    @Override
    public final void updateStateModification(TLUpdatesState state) {
        differencesHandler.updateStateModification(state, false);
    }

    @Override
    public final void onTLUpdatesTooLong() {
        differencesHandler.getDifferences();
    }

    private void onTLFakeUpdate(TLFakeUpdate update) {
        onTLFakeUpdateCustom(update);
    }

    @Override
    public final void onTLAbsDifference(@NotNull TLAbsDifference absDifference) {
        onUsers(absDifference.getUsers());
        onChats(absDifference.getChats());
        absDifference.getOtherUpdates().stream().map(x -> {
            UpdateWrapper updateWrapper = new UpdateWrapper(x);
            updateWrapper.disablePtsCheck();
            updateWrapper.disableUpdatePts();
            updateWrapper.enableGettingDifferences();
            return updateWrapper;
        }).forEach(this::processUpdate);
    }

    @Override
    public final void onTLChannelDifferences(List<TLAbsUser> users, List<TLAbsMessage> messages, List<TLAbsUpdate> newUpdates, List<TLAbsChat> chats) {
        onUsers(users);
        onChats(chats);
        newUpdates.stream().map(x -> {
            UpdateWrapper updateWrapper = new UpdateWrapper(x);
            updateWrapper.disablePtsCheck();
            updateWrapper.disableUpdatePts();
            return updateWrapper;
        }).forEach(this::processUpdate);
    }

    @Override
    public final void onUsers(List<TLAbsUser> users) {
        onUsersCustom(users);
    }

    @Override
    public final void onChats(List<TLAbsChat> chats) {
        onChatsCustom(chats);
    }

    protected abstract void onTLUpdateChatParticipantsCustom(TLUpdateChatParticipants update);
    protected abstract void onTLUpdateNewMessageCustom(TLUpdateNewMessage update);
    protected abstract void onTLUpdateChannelNewMessageCustom(TLUpdateChannelNewMessage update);
    protected abstract void onTLUpdateChannelCustom(TLUpdateChannel update);
    protected abstract void onTLUpdateBotInlineQueryCustom(TLUpdateBotInlineQuery update);
    protected abstract void onTLUpdateBotInlineSendCustom(TLUpdateBotInlineSend update);
    protected abstract void onTLUpdateChannelMessageViewsCustom(TLUpdateChannelMessageViews update);
    protected abstract void onTLUpdateChannelPinnedMessageCustom(TLUpdateChannelPinnedMessage update);
    protected abstract void onTLUpdateChatAdminCustom(TLUpdateChatAdmin update);
    protected abstract void onTLUpdateChatParticipantAddCustom(TLUpdateChatParticipantAdd update);
    protected abstract void onTLUpdateChatParticipantAdminCustom(TLUpdateChatParticipantAdmin update);
    protected abstract void onTLUpdateChatParticipantDeleteCustom(TLUpdateChatParticipantDelete update);
    protected abstract void onTLUpdateChatUserTypingCustom(TLUpdateChatUserTyping update);
    protected abstract void onTLUpdateContactLinkCustom(TLUpdateContactLink update);
    protected abstract void onTLUpdateContactRegisteredCustom(TLUpdateContactRegistered update);
    protected abstract void onTLUpdateDcOptionsCustom(TLUpdateDcOptions update);
    protected abstract void onTLUpdateDeleteChannelMessagesCustom(TLUpdateDeleteChannelMessages update);
    protected abstract void onTLUpdateDeleteMessagesCustom(TLUpdateDeleteMessages update);
    protected abstract void onTLUpdateEditChannelMessageCustom(TLUpdateEditChannelMessage update);
    protected abstract void onTLUpdateMessageIdCustom(TLUpdateMessageId update);
    protected abstract void onTLUpdateNewStickerSetCustom(TLUpdateNewStickerSet update);
    protected abstract void onTLUpdateNotifySettingsCustom(TLUpdateNotifySettings update);
    protected abstract void onTLUpdatePrivacyCustom(TLUpdatePrivacy update);
    protected abstract void onTLUpdateReadChannelInboxCustom(TLUpdateReadChannelInbox update);
    protected abstract void onTLUpdateReadMessagesContentsCustom(TLUpdateReadMessagesContents update);
    protected abstract void onTLUpdateReadMessagesInboxCustom(TLUpdateReadMessagesInbox update);
    protected abstract void onTLUpdateReadMessagesOutboxCustom(TLUpdateReadMessagesOutbox update);
    protected abstract void onTLUpdateSavedGifsCustom(TLUpdateSavedGifs update);
    protected abstract void onTLUpdateServiceNotificationCustom(TLUpdateServiceNotification update);
    protected abstract void onTLUpdateStickerSetsCustom(TLUpdateStickerSets update);
    protected abstract void onTLUpdateStickerSetsOrderCustom(TLUpdateStickerSetsOrder update);
    protected abstract void onTLUpdateUserBlockedCustom(TLUpdateUserBlocked update);
    protected abstract void onTLUpdateUserNameCustom(TLUpdateUserName update);
    protected abstract void onTLUpdateUserPhoneCustom(TLUpdateUserPhone update);
    protected abstract void onTLUpdateUserPhotoCustom(TLUpdateUserPhoto update);
    protected abstract void onTLUpdateUserStatusCustom(TLUpdateUserStatus update);
    protected abstract void onTLUpdateUserTypingCustom(TLUpdateUserTyping update);
    protected abstract void onTLUpdateWebPageCustom(TLUpdateWebPage update);
    protected abstract void onTLFakeUpdateCustom(TLFakeUpdate update);
    protected abstract void onTLUpdateShortMessageCustom(TLUpdateShortMessage update);
    protected abstract void onTLUpdateShortChatMessageCustom(TLUpdateShortChatMessage update);
    protected abstract void onTLUpdateShortSentMessageCustom(TLUpdateShortSentMessage update);
    protected abstract void onTLUpdateBotCallbackQueryCustom(TLUpdateBotCallbackQuery update);
    protected abstract void onTLUpdateEditMessageCustom(TLUpdateEditMessage update);
    protected abstract void onTLUpdateInlineBotCallbackQueryCustom(TLUpdateInlineBotCallbackQuery update);
    protected abstract void onUsersCustom(List<TLAbsUser> users);
    protected abstract void onChatsCustom(List<TLAbsChat> chats);
    protected abstract void onTLUpdateEncryptionCustom(TLUpdateEncryption update);
    protected abstract void onTLUpdateEncryptedMessagesReadCustom(TLUpdateEncryptedMessagesRead update);
    protected abstract void onTLUpdateNewEncryptedMessageCustom(TLUpdateNewEncryptedMessage update);
    protected abstract void onTLUpdateEncryptedChatTypingCustom(TLUpdateEncryptedChatTyping update);
    protected abstract void onTLUpdateConfigCustom(TLUpdateConfig update);
    protected abstract void onTLUpdateDraftMessageCustom(TLUpdateDraftMessage update);
    protected abstract void onTLUpdatePtsChangedCustom(TLUpdatePtsChanged update);
    protected abstract void onTLUpdateReadChannelOutboxCustom(TLUpdateReadChannelOutbox update);
    protected abstract void onTLUpdateReadFeaturedStickersCustom(TLUpdateReadFeaturedStickers update);
    protected abstract void onTLUpdateRecentStickersCustom(TLUpdateRecentStickers update);
    protected abstract void onTLUpdateChannelWebPageCustom(TLUpdateChannelWebPage update);
    protected abstract void onTLUpdatePhoneCallCustom(TLUpdatePhoneCall update);
    protected abstract void onTLUpdateDialogPinnedCustom(TLUpdateDialogPinned update);
    protected abstract void onTLUpdatePinnedDialogsCustom(TLUpdatePinnedDialogs update);
    protected abstract void onTLUpdateBotWebhookJSONCustom(TLUpdateBotWebhookJSON update);
    protected abstract void onTLUpdateBotWebhookJSONQueryCustom(TLUpdateBotWebhookJSONQuery update);
    protected abstract void onTLUpdateBotShippingQueryCustom(TLUpdateBotShippingQuery update);
    protected abstract void onTLUpdateBotPrecheckoutQueryCustom(TLUpdateBotPrecheckoutQuery update);
    protected abstract void onTLUpdateChannelTooLongCustom(TLUpdateChannelTooLong update);
}
