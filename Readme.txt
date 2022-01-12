1. Team members and work distribution
Tien Nguyen (s3757934) (33%): UI designs, XML layouts, Notification service, Phone broadcast, Setup all activities
Cuong Nguyen (s3748840) (33%): Data model, FireStorage, Firebase, near realtime update using Firestore listeners, Messaging
Tam Nguyen (s3747274) (33%): Feedback, Create notifications, Input validation, Profile permission

2. Functionalities
- Sign in
    - validation
- Sign up
    - validation
    - auto fill sign in after successfully sign up
- Log out
- Home
    - Navigation bar to profile, explore and notification
    - sent requests list, review requests list, groups list
- Notification
    - received notification for new requests, changing requests/groups status, receive new feedback
    - click notification to go to the corresponding page
- Profile
    - update personal information including avatar
        - remove courses auto leave all corresponding groups and reject/cancel all relevant requests
    - validation
- Explore
    - view people studying the same courses that not currently in the same group or have pending requests for those courses
    - either pass, request, or give feedback
- Request
    - enter a message, select courses and send requests
    - validation
- Review request
    - either reject or approve for received requests,
    - cancel for sent requests
    - auto navigate to group view after approval
    - click to each avatar to view the profile
- Group
    - view all members, click to each members to view their profile
    - either leave, close group and give feedback for other teammates or join messaging
- Messaging
    - near real time messaging for members between a group
- Feedback
    - input feedback for one or a list of teammate(s)
- Near real time data update
    - data changes from database or from other devices are reflected to the current device near real time
- Confirmation boxes
    - confirmation for taking actions on requests/groups or removing courses
- Suggestion
    - based on incoming and outgoing phone broadcast to suggest teammate from personal contacts who study the same courses

3. Technology use
- Firebase Firestore
    - Snapshot listeners
- Firebase Authentication
- Firebase Storage
- Broadcast Receiver
- Service for notification listeners'
- Picasso to insert avatar
- EmailValidator

4. Open issues and known bugs they haven't fixed
- App might crashes but don't know why since they are not stably reproducible, probably due to machine performance fluctuation related to snapshot listeners for realtime update