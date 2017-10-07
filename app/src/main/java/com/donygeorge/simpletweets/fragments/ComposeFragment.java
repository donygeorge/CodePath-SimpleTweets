package com.donygeorge.simpletweets.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.donygeorge.simpletweets.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ComposeFragment extends DialogFragment {

    @BindView(R.id.etTweet)
    EditText etTweet;
    @BindView(R.id.btCompose)
    Button btCompose;

    private ComposeFragmentListener mListener;
    private static final String TEXT_KEY = "text";

    public ComposeFragment() {
        // Required empty public constructor
    }

    public static  ComposeFragment newInstance(String text) {
        ComposeFragment fragment = new ComposeFragment();
        Bundle args = new Bundle();
        if (text != null) {
            args.putString(TEXT_KEY, text);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compose, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        String text = getArguments().getString(TEXT_KEY);
        if (text != null) {
            etTweet.setText(text);
        }

        btCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = etTweet.getText().toString();
                if (text.length() <= 140) {
                    mListener.postTweet(text);
                    etTweet.setText("");
                    dismiss();
                }
                // TODO: Handle the error case
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Fragment parent = getTargetFragment();
        if (parent instanceof ComposeFragmentListener) {
            mListener = (ComposeFragmentListener) parent;
        } else {
            throw new RuntimeException(parent.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface ComposeFragmentListener {
        void postTweet(String text);
    }
}
