package com.tritonmon.fragment.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.model.Trade;
import com.tritonmon.staticmodel.Pokemon;

public class ViewTradeDialog extends DialogFragment {

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
//        public Dialog onCreateDialog(DialogFragment dialog, Bundle savedInstanceState);
        public void onViewTradeDialogPositiveClick(DialogFragment dialog, Trade trade);
        public void onViewTradeDialogNeutralClick(DialogFragment dialog, Trade trade);
        public void onViewTradeDialogNegativeClick(DialogFragment dialog, Trade trade);
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final Trade trade = (Trade) getArguments().getParcelable("trade");
        builder.setMessage(Constant.userData.get(trade.getOffererUsersId()).getName() + " has offered you a trade: their "
        + "Level " + trade.getOfferLevel() + " " + Constant.pokemonData.get(trade.getOfferPokemonId()).getName() + " for your "
        + "Level " + trade.getListerLevel() + " " + Constant.pokemonData.get(trade.getListerPokemonId()).getName() + ".")
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity
                        mListener.onViewTradeDialogPositiveClick(ViewTradeDialog.this, trade);
                    }
                })
                .setNeutralButton("Decide later", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity
                        mListener.onViewTradeDialogNeutralClick(ViewTradeDialog.this, trade);
                    }
                })
                .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the negative button event back to the host activity
                        mListener.onViewTradeDialogNegativeClick(ViewTradeDialog.this, trade);
                    }
                });
        return builder.create();
    }
}
