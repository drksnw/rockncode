from django.conf.urls import url

from . import views

app_name = 'tweets'
urlpatterns = [
    url(r'^$', views.Index.as_view(), name="index"),
    url(r'^user/(\w+)/$', views.Profile.as_view()),
    url(r'^user/(\w+)/post/$', views.PostTweet.as_view()),
    url(r'^hashtag/(\w+)/$', views.HashTagCloud.as_view()),
    url(r'^search/$', views.Search.as_view()),
]