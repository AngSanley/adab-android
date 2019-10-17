package com.ambinusian.adab.ui.student;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ambinusian.adab.manager.APIManager;
import com.ambinusian.adab.manager.NetworkHelper;
import com.ambinusian.adab.preferences.UserPreferences;
import com.ambinusian.adab.recyclerview.course.CourseAdapter;
import com.ambinusian.adab.recyclerview.course.CourseModel;
import com.ambinusian.adab.R;
import com.ambinusian.adab.recyclerview.discussion.DiscussionAdapter;
import com.ambinusian.adab.recyclerview.discussion.DiscussionModel;
import com.ambinusian.adab.recyclerview.nextorlatestclass.NextOrLatestClassAdapter;
import com.ambinusian.adab.recyclerview.nextorlatestclass.NextOrLatestClassModel;
import com.ambinusian.adab.room.ClassDatabase;
import com.ambinusian.adab.room.ClassEntity;
import com.ambinusian.adab.utility.TextUtility;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class FragmentHome extends Fragment {

    private RecyclerView coursesRecyclerView, discussionRecyclerView;
    private ArrayList<CourseModel> coursesList;
    private ArrayList<DiscussionModel> discussionList;
    private LinearLayout welcomeLayout;
    private CardView liveLayout;
    private ImageView liveClassIcon;
    private LinearLayoutManager linearLayoutManager;
    private TextView liveClassTitle, liveCourse, liveClassMeeting;
    private TextView welcomeTitle;
    private ImageView nextClassIcon;
    private TextView nextClassTime, nextClassTitle, nextCourse, nextClassSession;
    private Chip nextCourseCode, nextClassCode, nextClassType;
    private Boolean hasLiveClass;
    private MaterialButton seeAllLatestClass, seeAllNextClass, seeAllDiscussion;
    private LinearLayout yourNextClassLayout;
    private LinearLayout latestClassLayout;
    private ClassDatabase db;
    private UserPreferences userPreferences;
    private ClassEntity liveClass;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        coursesRecyclerView = view.findViewById(R.id.rv_courses);
        discussionRecyclerView = view.findViewById(R.id.rv_discussions);
        liveLayout = view.findViewById(R.id.liveLayout);
        liveClassIcon = view.findViewById(R.id.liveClassIcon);
        liveClassTitle = view.findViewById(R.id.tv_liveClassTitle);
        liveCourse = view.findViewById(R.id.tv_liveCourse);
        liveClassMeeting = view.findViewById(R.id.tv_liveClassMeeting);
        welcomeLayout = view.findViewById(R.id.welcomeLayout);
        welcomeTitle = view.findViewById(R.id.tv_welcomeTitle);
        nextClassIcon = view.findViewById(R.id.nextClassIcon);
        nextClassTime = view.findViewById(R.id.tv_nextClassTime);
        nextClassTitle = view.findViewById(R.id.tv_nextClassTitle);
        nextCourse = view.findViewById(R.id.tv_nextCourse);
        nextClassSession = view.findViewById(R.id.tv_nextClassSession);
        nextCourseCode = view.findViewById(R.id.chip_nextCourseCode);
        nextClassCode = view.findViewById(R.id.chip_nextClassCode);
        nextClassType = view.findViewById(R.id.chip_nextClassType);
        nextClassType = view.findViewById(R.id.chip_nextClassType);
        seeAllNextClass = view.findViewById(R.id.see_all_next_class);
        seeAllLatestClass = view.findViewById(R.id.see_all_latest_class);
        seeAllDiscussion = view.findViewById(R.id.see_all_discussion);
        yourNextClassLayout = view.findViewById(R.id.your_next_class_layout);
        latestClassLayout = view.findViewById(R.id.latest_class);
        db = ClassDatabase.getDatabase(getContext());
        coursesList = new ArrayList<>();
        discussionList = new ArrayList<>();
        hasLiveClass = false;
        userPreferences = new UserPreferences(getContext());

        // set visibility gone
        coursesRecyclerView.setVisibility(View.GONE);

        // get classes data
        db.classDAO().getAllClass().observe(getActivity(), new Observer<List<ClassEntity>>() {
            @Override
            public void onChanged(List<ClassEntity> classEntities) {
                liveClass = null;
                for(ClassEntity classEntity : classEntities){
                    Date date1 = null;
                    Date date2 = null;
                    try {
                        date1 = new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(classEntity.getSessionStartDate());
                        date2 = new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(classEntity.getSessionEndDate());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if(Calendar.getInstance().getTime().after(date1) && Calendar.getInstance().getTime().before(date2)){
                        liveClass = classEntity;
                        break;
                    }
                }

                if(liveClass == null){
                    welcomeLayout.setVisibility(View.VISIBLE);
                    liveLayout.setVisibility(View.GONE);
                    welcomeTitle.setText(getString(R.string.welcome_title, TextUtility.toTitleCase(userPreferences.getUserName().toUpperCase())));
                }
                else{
                    liveLayout.setVisibility(View.VISIBLE);
                    welcomeLayout.setVisibility(View.GONE);
                    liveClassIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_class_59_pencilpaper));
                    liveClassTitle.setText((String) liveClass.getTopicTitle());
                    liveCourse.setText((String) liveClass.getCourseName());
                    String meeting = getString(R.string.class_session) + " " + liveClass.getSessionTh();
                    liveClassMeeting.setText(meeting);

                    liveLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getContext(), ActivityLive.class);
                            int sessionId = liveClass.getSessionId();

                            //set all data to bundle
                            Bundle bundle = new Bundle();
                            bundle.putInt("session_id", sessionId);

                            //set bundle to the intent
                            intent.putExtras(bundle);

                            //go to ActivityLive
                            startActivity(intent);
                        }
                    });
                }

                int nextClass;
                Date currentDate  = Calendar.getInstance().getTime();

                //set list data for recycler view
                Date date = null;
                for(nextClass=0;nextClass<classEntities.size();nextClass++){
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd H:mm:ss");
                    try {
                        date = format.parse(classEntities.get(nextClass).getSessionStartDate());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(currentDate.before(date)) {
                        break;
                    }
                }

                //for next class layout
                if(nextClass == classEntities.size()){
                    yourNextClassLayout.setVisibility(View.GONE);
                }
                else {
                    //Next Class
                    ClassEntity next_class_info = classEntities.get(nextClass);
                    nextClassIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_class_56_pencilnote));
                    nextClassTime.setText(new SimpleDateFormat("EEEE, d MMMM YYYY H:MM").format(date));
                    nextClassTitle.setText(next_class_info.getTopicTitle());
                    nextCourse.setText(next_class_info.getCourseName());
                    nextClassSession.setText("Session "+next_class_info.getSessionTh());
                    nextCourseCode.setText(next_class_info.getCourseId());
                    nextClassCode.setText(next_class_info.getClassName());
                    nextClassType.setText(next_class_info.getSessionMode());
                }

                //for latest class layout
                if(nextClass == 0){
                    latestClassLayout.setVisibility(View.GONE);
                }
                else {
                    for (int i = nextClass - 1; i >= 0; i--) {
                        Date class_date = null;
                        try {
                            class_date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(classEntities.get(i).getSessionStartDate());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        coursesList.add(new CourseModel(classEntities.get(i).getSessionId(),
                                1,
                                new SimpleDateFormat("EEEE, d MMMM YYYY h:mm").format(class_date),
                                classEntities.get(i).getTopicTitle(),
                                classEntities.get(i).getCourseName(),
                                getString(R.string.class_session) + " " + classEntities.get(i).getSessionTh(),
                                classEntities.get(i).getCourseId(),
                                classEntities.get(i).getClassName(),
                                classEntities.get(i).getSessionMode()
                        ));
                    }

                    //set adapter for recycler view
                    coursesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    coursesRecyclerView.setAdapter(new CourseAdapter(getContext(), coursesList));
                    //set visible
                    coursesRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        });

        /*
        Discussion
        Fill using dummy data
        */
//
//        discussionList.add(new DiscussionModel("Thursday, 22 June 2019", "Saya Binusian 2022", "Chandra", "on Storage(MOBI6009)"));
//        discussionList.add(new DiscussionModel("Thursday, 22 June 2019", "Saya Binusian 2022", "Chandra", "on Storage(MOBI6009)"));
//        discussionList.add(new DiscussionModel("Thursday, 22 June 2019", "Saya Binusian 2022", "Chandra", "on Storage(MOBI6009)"));
//
//        //Set layout manager and adapter for discussionRecyclerView
//        linearLayoutManager = new LinearLayoutManager(getContext());
//        discussionRecyclerView.setLayoutManager(linearLayoutManager);
//
//        DiscussionAdapter adapter = new DiscussionAdapter(getContext(), discussionList);
//        discussionRecyclerView.setAdapter(adapter);
//
        // see all the next classes
        seeAllNextClass.setOnClickListener(view13 -> {
            Intent intent = new Intent(getActivity(), NextClassActivity.class);
            getActivity().startActivity(intent);
        });

        //see all the latetest classes
        seeAllLatestClass.setOnClickListener(view12 -> {
            Intent intent = new Intent(getActivity(), LatestClassActivity.class);
            getActivity().startActivity(intent);
        });

        seeAllDiscussion.setOnClickListener(view1 -> {
            // show fragment discussion
        });


    }


}