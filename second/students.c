
/********************************************************************/
/* Copyright (C) SSE-USTC, 2013-2014                                */
/*                                                                  */
/*  FILE NAME             :  students.c                             */
/*  PRINCIPAL AUTHOR      :  Mengning                               */
/*  SUBSYSTEM NAME        :  students                               */
/*  MODULE NAME           :  students                               */
/*  LANGUAGE              :  C                                      */
/*  TARGET ENVIRONMENT    :  ANY                                    */
/*  DATE OF FIRST RELEASE :  2012/12/30                             */
/*  DESCRIPTION           :  students info manager system           */
/********************************************************************/

/*
 * Revision log:
 *
 * Created by Mengning,2013/09/09
 * Debug and Test by Mengning,2013/09/13
 *
 */

#include<stdio.h>
#include<stdlib.h>

/* Record of every student */
typedef struct Student
{
    int id;
    int name;
    struct Student * next;
}tStudent;

tStudent * pHead = NULL;

int main()
{
    tStudent * pStudent = NULL;
    int i = 0;
    
    /* insert 1000 students */
    for (i=0;i<1000;i++)
    {
        pStudent        = (tStudent *)malloc(sizeof(tStudent));
        pStudent->id    = i;
        pStudent->name  = i;
        pStudent->next  = pHead;
        pHead           = pStudent;
    }
    
    /* select 1000 times by student id */
    for (i=0;i<1000;i++)
    {
        /* random() return long int value,0 to (2^31 - 1) */
        int id = random() % 1000; 
        pStudent = pHead;
        while(pStudent->id != id)
        {
            pStudent = pStudent->next;
        }
        printf("find student %d\n",pStudent->id);
    }    
}