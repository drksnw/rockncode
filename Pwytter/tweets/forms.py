from django import forms

class TweetForm(forms.Form):
    text = forms.CharField(widget=forms.Textarea(attrs={'rows': 1, 'cols': 85}), max_length=160)
    country = forms.CharField(widget=forms.HiddenInput(), initial="CH")

class SearchForm(forms.Form):
    query = forms.CharField(label="Enter a keyword to search for", widget=forms.TextInput(attrs={'size': 32, 'class': 'form-control'}))

