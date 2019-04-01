package study.android.sampletodo_m_kuro;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

class ShowRemindDialog extends BaseDialog {

    private static final String LOG_TAG = ShowRemindDialog.class.getSimpleName();

    private DialogInterface.OnClickListener mCompleteListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {

        }
    };

    ShowRemindDialog(Context context) {
        super(context);
    }

    void init() {
        inflate(R.layout.show_dialog);
        setPositiveButton(null);
    }

    @Override
    BaseDialog createDialogLayout(RemindItem item) {
        View layoutView = getLayoutView();

        if (item != null) {
            if (item.getName() != null || !item.getName().isEmpty()) {
                TextView name = layoutView.findViewById(R.id.remind_name_edit);
                name.setText(item.getName());
            }

            if (item.getDate() != null || !item.getDate().isEmpty()) {
                TextView date = layoutView.findViewById(R.id.remind_date_edit);
                date.setText(item.getDate());
            }

            if (item.getPlace() != null || !item.getPlace().isEmpty()) {
                TextView place = layoutView.findViewById(R.id.remind_place_edit);
                place.setText(item.getPlace());
            }

            if (item.getMemo() != null || !item.getMemo().isEmpty()) {
                TextView memo = layoutView.findViewById(R.id.remind_memo_edit);
                memo.setText(item.getMemo());
            }
        }
        return this;
    }
}
