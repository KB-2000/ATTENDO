package com.example.ml2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ResultDialog extends DialogFragment {
    Button okBtn;
    TextView resultTextView;

    @Nullable
    @Override
    public View
    onCreateView(@NonNull LayoutInflater inflater,
                 @Nullable ViewGroup container,
                 @Nullable Bundle savedInstanceState)
    {

        // importing View so as to inflate
        // the layout of our result dialog
        // using layout inflater.
        View view = inflater.inflate(
                R.layout.fragment_resultdialog, container,
                false);
        String resultText = "";

        // finding the elements by their id's.
        okBtn = view.findViewById(R.id.result_ok_button);
        resultTextView
                = view.findViewById(R.id.result_text_view);

        // To get the result text
        // after final face detection
        // and append it to the text view.
        Bundle bundle = getArguments();
        resultText = bundle.getString(
                LCOFaceDetection.RESULT_TEXT);
        resultTextView.setText(resultText);

        // Onclick listener so as
        // to make a dismissable button
        okBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        dismiss();
                    }
                });
        return view;
    }
}

