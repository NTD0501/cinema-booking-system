package com.example.cinemabookingsystem.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.CompoundButton
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cinemabookingsystem.MyApplication
import com.example.cinemabookingsystem.R
import com.example.cinemabookingsystem.adapter.BookingHistoryAdapter
import com.example.cinemabookingsystem.adapter.BookingHistoryAdapter.IClickQRListener
import com.example.cinemabookingsystem.constant.GlobalFunction.gentQRCodeFromString
import com.example.cinemabookingsystem.databinding.FragmentBookingBinding
import com.example.cinemabookingsystem.listener.IOnSingleClickListener
import com.example.cinemabookingsystem.model.BookingHistory
import com.example.cinemabookingsystem.prefs.DataStoreManager
import com.example.cinemabookingsystem.util.DateTimeUtils.convertDateToTimeStamp
import com.example.cinemabookingsystem.util.DateTimeUtils.getLongCurrentTimeStamp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*

class PromotionFragment : Fragment() {
    private var mFragmentBookingBinding: FragmentBookingBinding? = null
    private var mListBookingHistory: MutableList<BookingHistory>? = null
    private var mBookingHistoryAdapter: BookingHistoryAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mFragmentBookingBinding = FragmentBookingBinding.inflate(inflater, container, false)
        getListBookingHistory(false)
        mFragmentBookingBinding!!.chbBookingUsed.setOnCheckedChangeListener { _: CompoundButton?,
                                                                              isChecked: Boolean -> getListBookingHistory(isChecked) }
        return mFragmentBookingBinding!!.root
    }

    private fun getListBookingHistory(isUsed: Boolean) {
        if (activity == null) {
            return
        }
        MyApplication[activity].getBookingDatabaseReference().addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (mListBookingHistory != null) {
                    mListBookingHistory!!.clear()
                } else {
                    mListBookingHistory = ArrayList()
                }
                for (dataSnapshot in snapshot.children) {
                    val bookingHistory = dataSnapshot.getValue(BookingHistory::class.java)
                    if (bookingHistory != null
                            && DataStoreManager.getUser()?.email.equals(bookingHistory.user)) {
                        val isExpire = convertDateToTimeStamp(bookingHistory.date) < getLongCurrentTimeStamp()
                        if (isUsed) {
                            if (isExpire || bookingHistory.isUsed) {
                                mListBookingHistory!!.add(0, bookingHistory)
                            }
                        } else {
                            if (!isExpire && !bookingHistory.isUsed) {
                                mListBookingHistory!!.add(0, bookingHistory)
                            }
                        }
                    }
                }
                displayListBookingHistory()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun displayListBookingHistory() {
        if (activity == null) {
            return
        }
        val linearLayoutManager = LinearLayoutManager(activity)
        mFragmentBookingBinding!!.rcvBookingHistory.layoutManager = linearLayoutManager
        mBookingHistoryAdapter = BookingHistoryAdapter(activity, false,
                mListBookingHistory, object : IClickQRListener {
            override fun onClickOpenQrCode(id: String?) {
                showDialogConfirmBooking(id)
            }
        }, null)
        mFragmentBookingBinding!!.rcvBookingHistory.adapter = mBookingHistoryAdapter
    }

    private fun showDialogConfirmBooking(id: String?) {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.layout_dialog_qr_code)
        val window = dialog.window
        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)

        // Get view
        val imgClose = dialog.findViewById<ImageView>(R.id.img_close)
        val imgQrCode = dialog.findViewById<ImageView>(R.id.img_qr_code)

        // Set data
        gentQRCodeFromString(imgQrCode, id)

        // Set listener
        imgClose.setOnClickListener(object : IOnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                dialog.dismiss()
            }
        })
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mBookingHistoryAdapter != null) mBookingHistoryAdapter!!.release()
    }
}