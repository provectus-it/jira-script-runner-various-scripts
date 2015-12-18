import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.issue.comments.CommentManager
import java.util.regex.Pattern
import java.util.regex.Matcher
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
//define issue for test porposes
//IssueManager im = ComponentAccessor.getIssueManager();
//MutableIssue issue = im.getIssueObject("TSD-66");
def commentManager = ComponentAccessor.getCommentManager()
def comment = commentManager.getComments(issue).last().body
CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()
def CCNum = customFieldManager.getCustomFieldObjects(issue).find {it.name == 'CC List'}
List<ApplicationUser> newCC=[]
if (issue.getCustomFieldValue(CCNum)){
    newCC = issue.getCustomFieldValue(CCNum)
    }
def regexp = /\[~(.+?)\]/
Pattern pattern = Pattern.compile(regexp)
Matcher matcher = pattern.matcher(comment.toString())
List<ApplicationUser> user=[]
def userUtil = ComponentAccessor.getUserUtil()
int count=0
while (matcher.find()) {
    user[count] = userUtil.getUserByName(matcher.group(1))
        if (user[count]!=null){
        newCC.add(user[count])
        }
    count++
    }
ModifiedValue mVal = new ModifiedValue(issue.getCustomFieldValue(CCNum), newCC)
CCNum.updateValue(null, issue, mVal, new DefaultIssueChangeHolder())