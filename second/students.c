
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
tStudent     * pStudent = NULL;
tLinkTable * pLinkTable = NULL;
tLinkTableNode *pNode = NULL;
tStudent * fStudent = NULL;
tLinkTable * fLinkTable = NULL;
tLinkTableNode * fNode = NULL;
int m = 0; //accept from terminal

void showInfo();
int Conditon(tLinkTableNode * pNode);

int main()
{
    int i = 0;
    int flag = 1; //flag for first while
    static int n_id = 0; //as student's id
    static int n_name = 0; //as student's name;
    int n = 0; //flag for switch
    int flag1 = 1;
    while (flag == 1)
    {
        showInfo();
        printf("please input the number:");
        scanf("%d",&n);
        switch(n)
        {
            /* insert n student */
            case 0:
                pLinkTable = CreateLinkTable();
                printf("please input the 'n_num' of the students:");
                scanf("%d", &n_id);
                for (i = 1; i <= n_id; i++)
                {
                    pNode  = (tLinkTableNode *)malloc(sizeof(tLinkTableNode));
                    pStudent        = (tStudent *)malloc(sizeof(tStudent));
                    pStudent->id    = i;
                    pStudent->name  = i;
                    pNode->data = pStudent;
                    if (!(SUCCESS == AddLinkTableNode(pLinkTable, pNode)))
                    {
                        printf("insert is FAILURE!!!!!\n");
                        break;
                    }
                }
                n_name = n_id;
                printf("insert the %d students\n", n_id);
                break;
            case 1:
                pNode  = (tLinkTableNode *)malloc(sizeof(tLinkTableNode));
                pStudent        = (tStudent *)malloc(sizeof(tStudent));
                pStudent->id    = ++n_id;
                pStudent->name  = ++n_name;
                pNode->data = pStudent;
                if (!(SUCCESS == AddLinkTableNode(pLinkTable, pNode)))
                {
                    printf("insert is FAILURE!!!!!\n");
                    break;
                }
                printf("add a student SUCCESS!!!\n");
                break;
            case 2:
                pNode = GetLinkTableHead(pLinkTable);
                printf("please input the student's id :");
	            scanf("%d", &m);
                while (1)
                {
                    flag1 = 1;
                    pStudent = (tStudent *)pNode->data;
                    if (pStudent->id == m)
                    {
                        if (DelLinkTableNode(pLinkTable, pNode) == SUCCESS)
                        {
                            fStudent = pStudent;
                            fNode = pNode;
                            free(fStudent);
                            free(fNode);
                            flag1 = 0;
                            printf("delete SUCCESS!!!\n");
                            break;
                        }
                    }
                    pNode = GetNextLinkTableNode(pLinkTable, pNode);
                }
                if (flag1 == 1)
                {
                    printf("the student does not exist !!!\n");
                }
                break;
            case 3:
                pNode = GetLinkTableHead(pLinkTable);
                printf("please input the student's id :");
	            scanf("%d", &m);
                while (1)
                {
                    pStudent = (tStudent *)pNode->data;
                    if (pStudent->id == m)
                    {
                        printf("please input the new id: ");
                        scanf("%d", &pStudent->id);
                        printf("please input the new name: ");
                        scanf("%d", &pStudent->name);
                        printf("congratulation!!!  successfully modified!!!\n");
                        break;
                    }
                    pNode = GetNextLinkTableNode(pLinkTable, pNode);
                }
                break;
            case 4:
                printf("please input student's id :");
                scanf("%d", &m);
                pNode = SearchLinkTableNode(pLinkTable, Conditon);
                pStudent = (tStudent *)pNode->data;
                printf("the student's id is : %d\n", pStudent->id);
                printf("the student's name is : %d\n", pStudent->name);
                break;
            case 5:
                pNode = GetLinkTableHead(pLinkTable);
                while (pNode != NULL)
                {
                    pStudent = (tStudent *)pNode->data;
                    printf("the student id and name is: %d and %d\n", pStudent->id,pStudent->name);
                    //printf("the student name is %d\n", pStudent->name);
                    pNode = GetNextLinkTableNode(pLinkTable, pNode);
                }
                break;
            case 6:
                flag = 0;
                break;
            default :
                printf("please input the right number\n");
        }
    }
}

void showInfo()
{
    printf("            -----------------------------\n");
    printf("              student management system\n");
    printf("            -----------------------------\n");
    printf("                0.insert n students\n");
    printf("                1.add one student\n");
    printf("                2.delete one student\n");
    printf("                3.change one student information\n");
    printf("                4.search a student\n");
    printf("                5.show all students information\n");
    printf("                6.exit\n");
    return;
}

int Conditon(tLinkTableNode * pNode)
{
    pStudent = (tStudent *)pNode->data;
    if (pStudent->id == m)
    {
        return SUCCESS;
    }
}
