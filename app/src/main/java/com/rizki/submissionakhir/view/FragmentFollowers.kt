package com.rizki.submissionakhir.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.rizki.submissionakhir.databinding.FragmentFollowersBinding
import com.rizki.submissionakhir.networking.ConfigApi
import com.rizki.submissionakhir.response.GithubUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentFollowers : Fragment() {

    private lateinit var binding : FragmentFollowersBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFollowersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val queryUsername = arguments?.getString(ARG_USERNAME)
        showFollowers(queryUsername?:"")
    }

    private fun showFollowers(username : String){
        showLoading(true)
        val client = ConfigApi.getServiceApi().getFollowersUser(username)
        client.enqueue(object: Callback<ArrayList<GithubUser>> {
            override fun onResponse(
                call: Call<ArrayList<GithubUser>>,
                response: Response<ArrayList<GithubUser>>
            ) {
                showLoading(false)
                val responseBody = response.body()
                if (responseBody != null){
                    binding.rvFollowers.layoutManager = LinearLayoutManager(activity)
                    val userAdapter = UsersAdapter(responseBody)
                    binding.rvFollowers.adapter = userAdapter

                    userAdapter.setOnItemClickCallback(object: UsersAdapter.OnItemClickCallback{
                        override fun onItemClicked(listUser: GithubUser) {
                            Toast.makeText(activity, listUser.Username, Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }

            override fun onFailure(call: Call<ArrayList<GithubUser>>, t: Throwable) {
                showLoading(false)
                Toast.makeText(activity,"Terjadi Sesuatu :/n ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        binding
    }

    private fun showLoading(isLoading : Boolean){
        if (isLoading){
            binding.progressBar.visibility = View.VISIBLE
        }else{
            binding.progressBar.visibility = View.GONE
        }
    }

    companion object {
        private const val ARG_USERNAME = "arg_username"


        @JvmStatic
        fun slideInstance(username: String) : FragmentFollowers{
            val fragment = FragmentFollowers()
            val bundle = Bundle()
            bundle.putString(ARG_USERNAME, username)
            fragment.arguments = bundle
            return fragment
        }
    }
}