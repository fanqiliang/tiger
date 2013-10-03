
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
#include"linktable.h"

/* Record of every student */
typedef struct Student
{
    int id;
    int name;
}tStudent;

tStudent * pHead = NULL;

void showInfo();

int main()
{
    tStudent     * pStudent = NULL;
    tLinkTable * pLinkTable = NULL;
    tLinkTableNode *pNode = NULL; 
    int i = 0;
    int n = 0;
    int num = 0;
    while(n == 0)
    {
        showInfo();
        printf("please input the right number:");
        scanf("%d",&n);
        switch(n)
        {
            case 0:
                pLinkTable = CreateLinkTable();
                printf("please input the 'num' of the students:");
                scanf("%d", &num);
                /* insert n students */
                for (i = 0; i < n;i++)
                {
                    pNode  = (tLinkTableNode *)malloc(sizeof(tLinkTableNode));
                    pStudent        = (tStudent *)malloc(sizeof(tStudent));
                    pStudent->id    = i;
                    pStudent->name  = i;
                    pNode->data = pStudent;
                    if (!(SUCCESS == AddLinkTableNode(pLinkTable, pNode)))
                    {
                        printf("insert is FAILURE!!!!!");
                        break;
                    }
                }
                break;
            default :
                return 1;
        }
    }
}

void showInfo()
{
    printf("            -----------------------------\n");
    printf("              student management system\n");
    printf("            -----------------------------\n");
    printf("                0.insert n students\n");
    return;
}
