from django.shortcuts import render
from django.http import HttpResponse, HttpResponseRedirect
from django.views.generic import View
from django.template import Context
from django.template.loader import render_to_string
from user_profile.models import User
from .models import Tweet, HashTag
from .forms import TweetForm, SearchForm
import json

# Create your views here.
class Index(View):
    def get(self, request):
        params = {}
        params["alltweets"] = Tweet.objects.all()
        return render(request, 'tweets/index.html', params)
    def post(self, request):
        return HttpResponse('I am called from a post request')

class Profile(View):
    def get(self, request, username):
        params = {}
        user = User.objects.get(username=username)
        tweets = Tweet.objects.filter(user=user)
        params["tweets"] = tweets
        params["user"] = user
        params["form"] = TweetForm
        return render(request, 'tweets/profile.html', params)

class PostTweet(View):
    def post(self, request, username):
        form = TweetForm(self.request.POST)
        if form.is_valid():
            user = User.objects.get(username=username)
            tweet = Tweet(text=form.cleaned_data['text'], user=user, country=form.cleaned_data['country'])
            tweet.save()
            words = form.cleaned_data['text'].split(" ")
            for word in words:
                if word[0] == "#":
                    hashtag, created = HashTag.objects.get_or_create(name=word[1:])
                    hashtag.tweet.add(tweet)
        return HttpResponseRedirect('/tweets/user/'+username)

class HashTagCloud(View):
    def get(self, request, hashtag):
        params = {}
        hashtag = HashTag.objects.get(name=hashtag)
        params["tweets"] = hashtag.tweet
        return render(request, 'tweets/hashtag.html', params)

class Search(View):
    def get(self, request):
        form = SearchForm()
        params = {}
        params["search"] = form
        return render(request, 'tweets/search.html', params)
    def post(self, request):
        form = SearchForm(request.POST)
        if form.is_valid():
            query = form.cleaned_data['query']
            tweets = Tweet.objects.filter(text__contains=query)
            context = Context({"query": query, "tweets": tweets})
            return_str = render_to_string('tweets/partials/_tweet_search.html', context)
            return HttpResponse(json.dumps(return_str), content_type="application/json")
        else:
            HttpResponseRedirect("/tweets/search")
