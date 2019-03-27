package study.android.sampletodo_m_kuro;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

abstract class BaseDialog extends AlertDialog.Builder {

    private View mDialogLayoutView;
    private MainActivity.CallbackListener mCallbackListener;

    BaseDialog(Context context) {
        super(context);
    }

    void inflate(int resource) {
        mDialogLayoutView = LayoutInflater.from(getContext()).inflate(resource, null);
    }

    View getLayoutView() {
        return mDialogLayoutView;
    }

    AlertDialog.Builder setPositiveButton(DialogInterface.OnClickListener listener) {
        return super.setPositiveButton(R.string.button_OK, listener);
    }

    AlertDialog.Builder setNegativeButton(DialogInterface.OnClickListener listener) {
        return super.setNegativeButton(R.string.button_Cancel, listener);
    }

    @Override
    public AlertDialog show() {
        super.setView(mDialogLayoutView);
        return super.show();
    }

    void registerCallbackListener(MainActivity.CallbackListener listener) {
        mCallbackListener = listener;
    }

    void unregisterCallbackListener(MainActivity.CallbackListener listener) {
        mCallbackListener = null;
    }

    void notifyOnPositiveButtonAction() {
        mCallbackListener.onNotifyPositiveAction();
    }

    void notifyOnPositiveButtonAction(RemindItem item) {
        mCallbackListener.onNotifyPositiveAction(item);
    }

    void notifyOnNegativeButtonAction() {
        mCallbackListener.onNotifyNegativeAction();
    }

    abstract void init();

    abstract BaseDialog createDialogLayout(RemindItem item);
}
