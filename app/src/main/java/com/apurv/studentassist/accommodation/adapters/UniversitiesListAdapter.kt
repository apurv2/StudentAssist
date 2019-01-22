package com.apurv.studentassist.accommodation.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.android.volley.toolbox.ImageLoader
import com.apurv.studentassist.R
import com.apurv.studentassist.accommodation.classes.AccommodationAdd
import com.apurv.studentassist.internet.Network
import kotlinx.android.synthetic.main.single_university_view.view.*

/**
 * Created by akamalapuri on 3/15/2017.
 */
class UniversitiesListAdapter(private val mUniversities: MutableList<AccommodationAdd>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    internal var mImage: ImageView? = null

    private val mImageLoader: ImageLoader
    private val network: Network = Network.getNetworkInstnace()


    init {
        mImageLoader = network.getmImageLoader()
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view = LayoutInflater.from(viewGroup?.context).inflate(R.layout.single_university_view, viewGroup, false)
        return UniversitiesViewHolder(view)

    }

    override fun onBindViewHolder(mUniversityRow: RecyclerView.ViewHolder, position: Int) {
        if (mUniversityRow is UniversitiesViewHolder) {

            (mUniversityRow as UniversitiesViewHolder).onBind(mUniversities.get(position))

//            loadImages(mUniversityRow, mUniversity.getUrls(), position)
        }
    }

//    private fun loadImages(holder: UniversitiesViewHolder, url: String, position: Int) {
//
//
//        mImageLoader.get(url, object : ImageLoader.ImageListener {
//            override fun onResponse(response: ImageLoader.ImageContainer, isImmediate: Boolean) {
//
//
//                val photo = response.bitmap
//                if (photo != null) {
//                    holder.universityPhoto.setImageBitmap(photo)
//                }
//            }
//
//            override fun onErrorResponse(error: VolleyError) {
//
//            }
//        })
//
//    }

//    fun updateList(list: MutableList<University>) {
//        mUniversities = list
//        notifyDataSetChanged()
//    }

    fun add(accommodationAdd: AccommodationAdd) {
        mUniversities.add(accommodationAdd)
        notifyItemInserted(mUniversities.size)
    }

    fun pop() {
        mUniversities.removeAt(mUniversities.size - 1)
        notifyItemRemoved(mUniversities.size)
    }

    fun addAll(universityList: MutableList<AccommodationAdd>) {

        mUniversities.addAll(universityList);
        notifyItemInserted(mUniversities.size);
    }

    fun clear() {

        for (i in mUniversities.indices.reversed()) {
            mUniversities.remove(mUniversities[i])
            notifyItemRemoved(mUniversities.size)
        }


    }

    override fun getItemCount(): Int {

        return mUniversities.size
    }


    internal inner class UniversitiesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        fun onBind(add: AccommodationAdd) {


            itemView.noOfRoomsFlashCards.setText(add.noOfRooms)
            itemView.apartmentAddress.setText(add.noOfRooms)
            itemView.cost.setText(add.cost)
            itemView.apartmentNameFlashCards.setText(add.apartmentName)


        }


        override fun onClick(v: View?) {
            if (v != null) {
//                val university = mUniversities[adapterPosition]
//                if (parentActivity.onTouch(university, v)) {
//
//                    university.setSelected(!university.isSelected())
//                    val roundTick = v.findViewById<View>(R.id.selectedPhoto)
//                    Utilities.toggleViewWithRevealAnimation(roundTick)
//                }

            }
        }
    }
}

