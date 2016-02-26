from django.db import models
from user_profile.models import User

# Create your models here.
class Tweet(models.Model):
    user = models.ForeignKey(User)
    text = models.CharField(max_length=160)
    created_date = models.DateTimeField(auto_now_add=True)
    country = models.CharField(max_length=30)
    is_active = models.BooleanField(default=True)

    def __str__(self):
        return self.text

class HashTag(models.Model):
    name = models.CharField(max_length=64, unique=True)
    tweet = models.ManyToManyField(Tweet)
    def __str__(self):
        return self.name