package com.example.teamder.model;

import static com.example.teamder.activity.RequestActivity.Status.approved;
import static com.example.teamder.activity.RequestActivity.Status.pending;
import static com.example.teamder.model.User.parseUser;
import static com.example.teamder.repository.RequestRepository.getRequestsByPartiesAndStatus;
import static com.example.teamder.repository.UserRepository.getUserById;
import static com.example.teamder.repository.UtilRepository.updateFieldToDb;

import com.example.teamder.repository.UserRepository;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;

public class ToVisitUserList {

    private static final User currentUser = CurrentUser.getInstance().getUser();
    private volatile static ToVisitUserList toVisitUserList;

    private final ArrayList<String> userIDs = new ArrayList<>();

    private ToVisitUserList() {
    }

    public static ToVisitUserList getInstance() {
        if (toVisitUserList == null) {
            synchronized (ToVisitUserList.class) {
                if (toVisitUserList == null) {
                    toVisitUserList = new ToVisitUserList();
                }
            }
        }
        return toVisitUserList;
    }

    public static int countIntersectCourses(User user) {
        ArrayList<String> intersectCourses = new ArrayList<>(user.getCourses());
        intersectCourses.retainAll(currentUser.getCourses());
        return intersectCourses.size();
    }

    public void removeAllUserIDs() {
        userIDs.removeAll(userIDs);
    }

    public void addUserID(String userID) {
        userIDs.add(userID);
    }

    public String getUserID(int index) {
        return userIDs.get(index);
    }

    public String getUserID() {
        return getUserID(0);
    }

    public void removeUserID() {
        if (userIDs.size() > 0) {
            userIDs.remove(0);
        }
    }

    public ArrayList<String> getUserIDs() {
        return userIDs;
    }

    public void resetList() {
        currentUser.setVisitedTeameeIDs(new ArrayList<String>());
        updateFieldToDb("users", currentUser.getId(), "visitedTeameeIDs", currentUser.getVisitedTeameeIDs());
        userIDs.removeAll(userIDs);
        if (currentUser.getCourses().size() > 0) {
            UserRepository.getOtherUsersByCourse(
                    currentUser.getCourses(),
                    (QuerySnapshot) -> {
                        for (DocumentSnapshot document : QuerySnapshot.getDocuments()) {
                            String userID = document.getId();
                            if (ableToVisit(userID)) {
                                checkIntersectCourse(userID);
                            }
                        }
                    }
            );
        }
    }

    private boolean ableToVisit(String userID) {
        return !toVisitUserList.getUserIDs().contains(userID)
                && !userID.equals(currentUser.getId())
                && !currentUser.getVisitedTeameeIDs().contains(userID);
    }

    private void checkIntersectCourse(String userID) {
        getUserById(userID, (documentSnapshot -> {
            User user = parseUser(documentSnapshot);
            ArrayList<String> parties = new ArrayList<>(Arrays.asList(userID, currentUser.getId()));
            getRequestsByPartiesAndStatus(pending.toString(), parties, (snapshot) -> {
                        final int[] requestNo = {snapshot.getDocuments().size()};
                        getRequestsByPartiesAndStatus(approved.toString(), parties, (documentSnapshots) -> {
                            requestNo[0] += documentSnapshots.getDocuments().size();
                            int courseAvailable = (countIntersectCourses(user) - requestNo[0]);
                            if (courseAvailable > 0) {
                                toVisitUserList.addUserID(userID);
                            }
                        });
                    }
            );
        }));
    }

}
