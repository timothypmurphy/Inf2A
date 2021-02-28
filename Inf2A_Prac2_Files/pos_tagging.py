# File: pos_tagging.py
# Template file for Informatics 2A Assignment 2:
# 'A Natural Language Query System in Python/NLTK'

# John Longley, November 2012
# Revised November 2013 and November 2014 with help from Nikolay Bogoychev
# Revised November 2015 by Toms Bergmanis


# PART B: POS tagging

from statements import *

# The tagset we shall use is:
# P  A  Ns  Np  Is  Ip  Ts  Tp  BEs  BEp  DOs  DOp  AR  AND  WHO  WHICH  ?

# Tags for words playing a special role in the grammar:

function_words_tags = [('a','AR'), ('an','AR'), ('and','AND'),
     ('is','BEs'), ('are','BEp'), ('does','DOs'), ('do','DOp'), 
     ('who','WHO'), ('which','WHICH'), ('Who','WHO'), ('Which','WHICH'), ('?','?')]
     # upper or lowercase tolerated at start of question.

function_words = [p[0] for p in function_words_tags]

def unchanging_plurals():
    # add code here
    plurals = []
    NNs = []
    NNSs = []
    with open("sentences.txt", "r") as f:
        for line in f:
            for words in line.split():
                (word,POS) = words.split("|")
                if POS == "NN":
                    if word not in NNs:
                        NNs.insert(word)
                if POS == "NNS":
                    if word not in NNSs:
                        NNSs.insert(word)
        
    for word in NNs:
        if word in NNSs:
            plurals.insert(word)
            
    return plurals

unchanging_plurals_list = unchanging_plurals()

def noun_stem (s):
    """extracts the stem from a plural noun, or returns empty string"""    
    # add code here
    if s in unchanging_plurals_list:
        return s
    elif re.match("[a-z]*men$",s):
        return s[:-2] + "an"
    else:
        return verb_stem(s)

def tag_word (lx,wd):
    """returns a list of all possible tags for wd relative to lx"""
    # add code here
    tag = []
    
    for(word,tag) in lx:
        if(tag == 'P'):
            if(word == wd):
                tag.insert(tag)

        if(tag == 'N'):
            if (noun_stem(wd) == word):
                tag.insert("Np")
            elif (word == wd):
                tag.insert("Ns")

        if(tag == 'A'):
            if (word == wd):
                tag.insert("A")

        if(tag == 'I'):
            if (verb_stem(wd) == word):
                tag.insert("Ip")
            elif (word == wd):
                tag.insert("Is")

        if(tag == 'T'):
            if (verb_stem(wd) == word):
                tag.insert("Tp")
            elif (word == wd):
                tag.insert("Ts")

    return tag

def tag_words (lx, wds):
    """returns a list of all possible taggings for a list of words"""
    if (wds == []):
        return [[]]
    else:
        tag_first = tag_word (lx, wds[0])
        tag_rest = tag_words (lx, wds[1:])
        return [[fst] + rst for fst in tag_first for rst in tag_rest]

# End of PART B.