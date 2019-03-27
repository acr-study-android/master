package study.android.sampletodo_m_kuro;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;

class EditRemindDialog extends BaseDialog {

    private static final String LOG_TAG = EditRemindDialog.class.getSimpleName();

    private DialogInterface.OnClickListener mPositiveButtonListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            View layoutView = getLayoutView();

            EditText editText = layoutView.findViewById(R.id.remind_name_edit);
            EditText editDate = layoutView.findViewById(R.id.remind_date_edit);
            EditText editPlace = layoutView.findViewById(R.id.remind_place_edit);
            EditText editMemo = layoutView.findViewById(R.id.remind_memo_edit);

            RemindItem item =
                    new RemindItem(editText.getText(),
                            editDate.getText(),
                            editPlace.getText(),
                            editMemo.getText());
            if (item.getName() == null || item.getName().isEmpty()) {
                createDialogLayout(item);
                show();
            }
            notifyOnPositiveButtonAction(item);
        }
    };

    EditRemindDialog(Context context) {
        super(context);
    }

    void init() {
        inflate(R.layout.edit_dialog);
        setPositiveButton(mPositiveButtonListener);
        setNegativeButton(null);
    }

    @Override
    BaseDialog createDialogLayout(RemindItem item) {
        View layoutView = getLayoutView();

        if (item != null) {
            if (item.getName() != null || !item.getName().isEmpty()) {
                EditText editName = layoutView.findViewById(R.id.remind_name_edit);
                editName.setText(item.getName());
            }

            if (item.getDate() != null || !item.getDate().isEmpty()) {
                EditText editDate = layoutView.findViewById(R.id.remind_date_edit);
                editDate.setText(item.getDate());
            }

            if (item.getPlace() != null || !item.getPlace().isEmpty()) {
                EditText editPlace = layoutView.findViewById(R.id.remind_place_edit);
                editPlace.setText(item.getPlace());
            }

            if (item.getMemo() != null || !item.getMemo().isEmpty()) {
                EditText editMemo = layoutView.findViewById(R.id.remind_memo_edit);
                editMemo.setText(item.getMemo());
            }
        }
        return this;
    }
}
