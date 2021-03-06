/*
 * ==========================================================================
 * Copyright (C) 2019-2021 HCL America, Inc. ( http://www.hcl.com/ )
 *                            All rights reserved.
 * ==========================================================================
 * Licensed under the  Apache License, Version 2.0  (the "License").  You may
 * not use this file except in compliance with the License.  You may obtain a
 * copy of the License at <http://www.apache.org/licenses/LICENSE-2.0>.
 *
 * Unless  required  by applicable  law or  agreed  to  in writing,  software
 * distributed under the License is distributed on an  "AS IS" BASIS, WITHOUT
 * WARRANTIES OR  CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the  specific language  governing permissions  and limitations
 * under the License.
 * ==========================================================================
 */
package com.hcl.domino.commons.design;

import java.text.MessageFormat;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Set;
import java.util.stream.Collectors;

import com.hcl.domino.commons.NotYetImplementedException;
import com.hcl.domino.commons.design.action.DefaultCopyToDatabaseAction;
import com.hcl.domino.commons.design.action.DefaultDeleteDocumentAction;
import com.hcl.domino.commons.design.action.DefaultFolderBasedAction;
import com.hcl.domino.commons.design.action.DefaultModifyByFormAction;
import com.hcl.domino.commons.design.action.DefaultModifyFieldAction;
import com.hcl.domino.commons.design.action.DefaultReplyAction;
import com.hcl.domino.commons.design.action.DefaultSendDocumentAction;
import com.hcl.domino.commons.design.action.DefaultSendMailAction;
import com.hcl.domino.commons.design.action.DefaultSendNewsletterAction;
import com.hcl.domino.commons.design.agent.DefaultFormulaAgentContent;
import com.hcl.domino.commons.design.agent.DefaultImportedJavaAgentContent;
import com.hcl.domino.commons.design.agent.DefaultJavaAgentContent;
import com.hcl.domino.commons.design.agent.DefaultSimpleActionAgentContent;
import com.hcl.domino.commons.util.InnardsConverter;
import com.hcl.domino.commons.util.StringUtil;
import com.hcl.domino.data.Document;
import com.hcl.domino.data.DominoDateTime;
import com.hcl.domino.design.DesignAgent;
import com.hcl.domino.design.action.FolderBasedAction;
import com.hcl.domino.design.action.ReadMarksAction;
import com.hcl.domino.design.action.RunAgentAction;
import com.hcl.domino.design.action.SimpleAction;
import com.hcl.domino.design.agent.AgentContent;
import com.hcl.domino.design.agent.AgentInterval;
import com.hcl.domino.design.agent.AgentTrigger;
import com.hcl.domino.design.agent.FormulaAgentContent;
import com.hcl.domino.design.agent.LotusScriptAgentContent;
import com.hcl.domino.misc.NotesConstants;
import com.hcl.domino.richtext.RichTextConstants;
import com.hcl.domino.richtext.RichTextRecordList;
import com.hcl.domino.richtext.records.CDActionByForm;
import com.hcl.domino.richtext.records.CDActionDBCopy;
import com.hcl.domino.richtext.records.CDActionDelete;
import com.hcl.domino.richtext.records.CDActionFolder;
import com.hcl.domino.richtext.records.CDActionFormula;
import com.hcl.domino.richtext.records.CDActionJavaAgent;
import com.hcl.domino.richtext.records.CDActionLotusScript;
import com.hcl.domino.richtext.records.CDActionModifyField;
import com.hcl.domino.richtext.records.CDActionNewsletter;
import com.hcl.domino.richtext.records.CDActionReadMarks;
import com.hcl.domino.richtext.records.CDActionReply;
import com.hcl.domino.richtext.records.CDActionRunAgent;
import com.hcl.domino.richtext.records.CDActionSendDocument;
import com.hcl.domino.richtext.records.CDActionSendMail;
import com.hcl.domino.richtext.records.RecordType.Area;
import com.hcl.domino.richtext.structures.AssistFieldStruct;
import com.hcl.domino.richtext.structures.AssistStruct;

/**
 * @since 1.0.18
 */
public class AgentImpl extends AbstractNamedDesignElement<DesignAgent> implements DesignAgent {

  public AgentImpl(final Document doc) {
    super(doc);
  }

  @Override
  public AgentContent getAgentContent() {
    switch (this.getAgentLanguage()) {
      case FORMULA: {
        // Find the first CDACTIONFORMULA and read the contents
        return this.getDocument().getRichTextItem(NotesConstants.ASSIST_ACTION_ITEM, Area.TYPE_ACTION)
            .stream()
            .filter(CDActionFormula.class::isInstance)
            .map(CDActionFormula.class::cast)
            .findFirst()
            .map(action -> {
              FormulaAgentContent.DocumentAction docAction;
              final Set<CDActionFormula.Flag> flags = action.getFlags();
              if (flags.contains(CDActionFormula.Flag.NEWCOPY)) {
                docAction = FormulaAgentContent.DocumentAction.CREATE;
              } else if (flags.contains(CDActionFormula.Flag.SELECTDOCS)) {
                docAction = FormulaAgentContent.DocumentAction.SELECT;
              } else {
                docAction = FormulaAgentContent.DocumentAction.MODIFY;
              }
              final String formula = action.getAction();
              return new DefaultFormulaAgentContent(docAction, formula);
            })
            .orElseThrow(() -> new IllegalStateException("Unable to find formula action data"));
      }
      case LS: {
        // Could be represented two ways: either as a CDACTIONLOTUSSCRIPT or as multiple
        // $AgentHScript text items
        final CDActionLotusScript action = this.getDocument().getRichTextItem(NotesConstants.ASSIST_ACTION_ITEM, Area.TYPE_ACTION)
            .stream()
            .filter(CDActionLotusScript.class::isInstance)
            .map(CDActionLotusScript.class::cast)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Unable to find LotusScript action data"));
        if (action.getScriptLength() == 0) {
          // This must be stored in $AgentHScript items
          return (LotusScriptAgentContent) () -> this.getDocument().allItems()
              .filter(item -> NotesConstants.AGENT_HSCRIPT_ITEM.equalsIgnoreCase(item.getName()))
              .map(item -> item.getValue().get(0))
              .map(String::valueOf)
              .collect(Collectors.joining());
        } else {
          return (LotusScriptAgentContent) () -> action.getScript();
        }
      }
      case SIMPLE_ACTION: {
        final List<SimpleAction> actions = this.getDocument().getRichTextItem(NotesConstants.ASSIST_ACTION_ITEM, Area.TYPE_ACTION)
            .stream()
            .map(record -> {
              if (record instanceof CDActionByForm) {
                final CDActionByForm action = (CDActionByForm) record;
                final Map<String, List<String>> modifications = new LinkedHashMap<>();
                for (final AssistFieldStruct field : action.getAssistFields()) {
                  modifications.put(field.getFieldName(), field.getValues());
                }
                return new DefaultModifyByFormAction(action.getFormName(), modifications);
              } else if (record instanceof CDActionDBCopy) {
                final CDActionDBCopy action = (CDActionDBCopy) record;
                return new DefaultCopyToDatabaseAction(action.getServerName(), action.getDatabaseName());
              } else if (record instanceof CDActionDelete) {
                return new DefaultDeleteDocumentAction();
              } else if (record instanceof CDActionFolder) {
                final CDActionFolder action = (CDActionFolder) record;
                FolderBasedAction.Type type;
                switch (action.getHeader().getSignature()) {
                  case RichTextConstants.SIG_ACTION_MOVETOFOLDER:
                    type = FolderBasedAction.Type.MOVE;
                    break;
                  case RichTextConstants.SIG_ACTION_REMOVEFROMFOLDER:
                    type = FolderBasedAction.Type.REMOVE;
                    break;
                  case RichTextConstants.SIG_ACTION_COPYTOFOLDER:
                  default:
                    type = FolderBasedAction.Type.COPY;
                    break;
                }
                return new DefaultFolderBasedAction(action.getFolderName(), action.getFlags(), type);
              } else if (record instanceof CDActionFormula) {
                final CDActionFormula action = (CDActionFormula) record;
                FormulaAgentContent.DocumentAction docAction;
                if (action.getFlags().contains(CDActionFormula.Flag.NEWCOPY)) {
                  docAction = FormulaAgentContent.DocumentAction.CREATE;
                } else if (action.getFlags().contains(CDActionFormula.Flag.SELECTDOCS)) {
                  docAction = FormulaAgentContent.DocumentAction.SELECT;
                } else {
                  docAction = FormulaAgentContent.DocumentAction.MODIFY;
                }
                return new DefaultFormulaAgentContent(docAction, action.getAction());
              } else if (record instanceof CDActionModifyField) {
                final CDActionModifyField action = (CDActionModifyField) record;
                return new DefaultModifyFieldAction(action.getFieldName(), action.getValue());
              } else if (record instanceof CDActionNewsletter) {
                final CDActionNewsletter action = (CDActionNewsletter) record;
                return new DefaultSendNewsletterAction(
                    action.getViewName(),
                    action.getTo(),
                    action.getCc(),
                    action.getBcc(),
                    action.getSubject(),
                    action.getBody(),
                    action.getGatherCount(),
                    action.getFlags());
              } else if (record instanceof CDActionSendMail) {
                final CDActionSendMail action = (CDActionSendMail) record;
                return new DefaultSendMailAction(
                    action.getTo(),
                    action.getCc(),
                    action.getBcc(),
                    action.getSubject(),
                    action.getBody(),
                    action.getFlags());
              } else if (record instanceof CDActionReadMarks) {
                final CDActionReadMarks action = (CDActionReadMarks) record;
                switch (action.getHeader().getSignature()) {
                  case (byte) RichTextConstants.SIG_ACTION_MARKUNREAD:
                    return (ReadMarksAction) () -> ReadMarksAction.Type.MARK_READ;
                  case (byte) RichTextConstants.SIG_ACTION_MARKREAD:
                  default:
                    return (ReadMarksAction) () -> ReadMarksAction.Type.MARK_UNREAD;
                }
              } else if (record instanceof CDActionReply) {
                final CDActionReply action = (CDActionReply) record;
                return new DefaultReplyAction(action.getFlags(), action.getBody());
              } else if (record instanceof CDActionRunAgent) {
                final CDActionRunAgent action = (CDActionRunAgent) record;
                final String agentName = action.getAgentName();
                return (RunAgentAction) () -> agentName;
              } else if (record instanceof CDActionSendDocument) {
                return new DefaultSendDocumentAction();
              }
              return null;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        return new DefaultSimpleActionAgentContent(actions);
      }
      case JAVA: {
        final CDActionJavaAgent action = this.getDocument().getRichTextItem(NotesConstants.ASSIST_ACTION_ITEM, Area.TYPE_ACTION)
            .stream()
            .filter(CDActionJavaAgent.class::isInstance)
            .map(CDActionJavaAgent.class::cast)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Unable to find Java action data"));
        return new DefaultJavaAgentContent(
            action.getClassName(),
            action.getCodePath(),
            Arrays.stream(action.getFileList().split("\\n")) //$NON-NLS-1$
                .filter(StringUtil::isNotEmpty)
                .collect(Collectors.toList()),
            Arrays.stream(action.getLibraryList().split("\\n")) //$NON-NLS-1$
                .filter(StringUtil::isNotEmpty)
                .collect(Collectors.toList()));
      }
      case IMPORTED_JAVA: {
        // Similar to above, but the file list is a flat collection of imported files
        // and the library list isn't used
        final CDActionJavaAgent action = this.getDocument().getRichTextItem(NotesConstants.ASSIST_ACTION_ITEM, Area.TYPE_ACTION)
            .stream()
            .filter(CDActionJavaAgent.class::isInstance)
            .map(CDActionJavaAgent.class::cast)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Unable to find Java action data"));
        return new DefaultImportedJavaAgentContent(
            action.getClassName(),
            action.getCodePath(),
            Arrays.stream(action.getFileList().split("\\n")) //$NON-NLS-1$
                .filter(StringUtil::isNotEmpty)
                .collect(Collectors.toList()));
      }
      default:
        throw new UnsupportedOperationException(MessageFormat.format("Unsupported agent type {0}", this.getAgentLanguage()));
    }
  }

  @Override
  public AgentLanguage getAgentLanguage() {
    short lang = this.getDocument().get(NotesConstants.ASSIST_TYPE_ITEM, short.class, (short) 0);
    if (lang == RichTextConstants.SIG_ACTION_FORMULAONLY) {
      lang = RichTextConstants.SIG_ACTION_FORMULA;
    }
    switch (lang) {
      case RichTextConstants.SIG_ACTION_JAVAAGENT:
        // Imported Java agents are distinguished by a $Flags value
        if (this.getFlags().contains(NotesConstants.DESIGN_FLAG_JAVA_AGENT_WITH_SOURCE)) {
          return AgentLanguage.JAVA;
        } else {
          return AgentLanguage.IMPORTED_JAVA;
        }
      case RichTextConstants.SIG_ACTION_LOTUSSCRIPT:
        return AgentLanguage.LS;
      case RichTextConstants.SIG_ACTION_FORMULAONLY:
        return AgentLanguage.FORMULA;
      case RichTextConstants.SIG_ACTION_FORMULA: {
        // This gets weird. Both FORMULA and FORMULAONLY have been observed to represent
        // formula
        // agents... but that's not all. If a Simple Action agent _starts_ with a
        // @Function Formula
        // action, then the $AssistType field is written as SIG_ACTION_FORMULA. Thus, we
        // need to
        // open the agent to check.
        // Currently, the best check is to see if it has more than just the two opening
        // records that
        // it shared with Formula agents
        final RichTextRecordList list = this.getDocument().getRichTextItem(NotesConstants.ASSIST_ACTION_ITEM, Area.TYPE_ACTION);
        if (list.size() > 2) {
          return AgentLanguage.SIMPLE_ACTION;
        }
        return AgentLanguage.FORMULA;
      }
      case RichTextConstants.SIG_ACTION_MODIFYFIELD:
      case RichTextConstants.SIG_ACTION_REPLY:
      case RichTextConstants.SIG_ACTION_SENDMAIL:
      case RichTextConstants.SIG_ACTION_DBCOPY:
      case RichTextConstants.SIG_ACTION_DELETE:
      case RichTextConstants.SIG_ACTION_BYFORM:
      case RichTextConstants.SIG_ACTION_MARKREAD:
      case RichTextConstants.SIG_ACTION_MARKUNREAD:
      case RichTextConstants.SIG_ACTION_MOVETOFOLDER:
      case RichTextConstants.SIG_ACTION_COPYTOFOLDER:
      case RichTextConstants.SIG_ACTION_REMOVEFROMFOLDER:
      case RichTextConstants.SIG_ACTION_NEWSLETTER:
      case RichTextConstants.SIG_ACTION_RUNAGENT:
      case RichTextConstants.SIG_ACTION_SENDDOCUMENT:
      case -1: // Set when there are no actions
        return AgentLanguage.SIMPLE_ACTION;
      default:
        throw new IllegalStateException(MessageFormat.format("Unknown language value {0}", Short.toUnsignedInt(lang)));
    }
  }

  private Optional<AssistStruct> getAssistInfo() {
    final Document doc = this.getDocument();
    if (doc.hasItem(NotesConstants.ASSIST_INFO_ITEM)) {
      final AssistStruct assistInfo = doc.get(NotesConstants.ASSIST_INFO_ITEM, AssistStruct.class, null);
      return Optional.ofNullable(assistInfo);
    }
    return Optional.empty();
  }

  @Override
  public Optional<DominoDateTime> getEndDate() {
    if (this.getTrigger() != AgentTrigger.SCHEDULED) {
      return Optional.empty();
    }
    return this.getAssistInfo()
        .map(AssistStruct::getEndDate)
        .map(dt -> dt.isValid() ? dt : null);
  }

  @Override
  public OptionalInt getInterval() {
    switch (this.getIntervalType()) {
      case DAYS:
      case MINUTES:
      case MONTH:
      case WEEK:
        return this.getAssistInfo()
            .map(info -> OptionalInt.of(info.getInterval()))
            .orElse(OptionalInt.empty());
      case NONE:
      case EVENT:
      default:
        return OptionalInt.empty();
    }
  }

  @Override
  public AgentInterval getIntervalType() {
    return this.getAssistInfo()
        .map(AssistStruct::getIntervalType)
        .orElse(AgentInterval.NONE);
  }

  @Override
  public OptionalLong getLastRunDuration() {
    // TODO Auto-generated method stub
    throw new NotYetImplementedException();
  }

  @Override
  public OptionalInt getRunDayOfMonth() {
    switch (this.getIntervalType()) {
      case MONTH:
        return this.getAssistInfo()
            .map(AssistStruct::getTime2)
            .map(OptionalInt::of)
            .orElse(OptionalInt.empty());
      case MINUTES:
      case DAYS:
      case WEEK:
      case EVENT:
      case NONE:
      default:
        return OptionalInt.empty();
    }
  }

  @Override
  public Optional<DayOfWeek> getRunDayOfWeek() {
    switch (this.getIntervalType()) {
      case WEEK:
        return this.getAssistInfo()
            .map(AssistStruct::getTime2)
            // Domino's week starts with Saturday=0, while we want 1-based with Monday=1
            .map(val -> DayOfWeek.of((val + 12) % 7 + 1));
      case MINUTES:
      case DAYS:
      case MONTH:
      case EVENT:
      case NONE:
      default:
        return Optional.empty();
    }
  }

  @Override
  public Optional<LocalTime> getRunEndLocalTime() {
    switch (this.getIntervalType()) {
      case MINUTES:
        return this.getAssistInfo()
            .map(AssistStruct::getTime2)
            .map(Integer::toUnsignedLong)
            .map(InnardsConverter::ticksToLocalTime);
      case DAYS:
      case WEEK:
      case MONTH:
      case EVENT:
      case NONE:
      default:
        return Optional.empty();
    }
  }

  @Override
  public Optional<LocalTime> getRunLocalTime() {
    switch (this.getIntervalType()) {
      case MINUTES:
      case DAYS:
      case WEEK:
      case MONTH:
        return this.getAssistInfo()
            .map(AssistStruct::getTime1)
            .map(Integer::toUnsignedLong)
            .map(InnardsConverter::ticksToLocalTime);
      case EVENT:
      case NONE:
      default:
        return Optional.empty();
    }
  }

  @Override
  public String getRunLocation() {
    return this.getDocument().get(NotesConstants.FILTER_MACHINE_NAME, String.class, ""); //$NON-NLS-1$
  }

  @Override
  public String getRunLog() {
    // TODO Auto-generated method stub
    throw new NotYetImplementedException();
  }

  @Override
  public List<String> getRunLogAsList() {
    // TODO Auto-generated method stub
    throw new NotYetImplementedException();
  }

  @Override
  public Optional<DominoDateTime> getStartDate() {
    if (this.getTrigger() != AgentTrigger.SCHEDULED) {
      return Optional.empty();
    }
    return this.getAssistInfo()
        .map(AssistStruct::getStartDate)
        .map(dt -> dt.isValid() ? dt : null);
  }

  @Override
  public AgentTrigger getTrigger() {
    return this.getAssistInfo()
        .map(AssistStruct::getTrigger)
        .orElse(AgentTrigger.NONE);
  }

  @Override
  public DesignAgent initializeAgentLanguage(final AgentLanguage lang) {
    int val;
    switch (Objects.requireNonNull(lang)) {
      case LS:
        val = RichTextConstants.SIG_ACTION_LOTUSSCRIPT;
        break;
      case JAVA:
      case IMPORTED_JAVA:
        val = RichTextConstants.SIG_ACTION_JAVA;
        break;
      case FORMULA:
        val = RichTextConstants.SIG_ACTION_FORMULAONLY;
        break;
      default:
        val = -1;
    }
    this.getDocument().replaceItemValue(NotesConstants.ASSIST_TYPE_ITEM, val);
    return this;
  }

  @Override
  public void initializeNewDesignNote() {
    this.setFlags("j3"); //$NON-NLS-1$
  }

  @Override
  public boolean isLastRunExceededTimeLimit() {
    // TODO Auto-generated method stub
    throw new NotYetImplementedException();
  }

  // *******************************************************************************
  // * Implementation utility methods
  // *******************************************************************************

  @Override
  public boolean isRunOnWeekends() {
    return this.getAssistInfo()
        .map(info -> !info.getFlags().contains(AssistStruct.Flag.NOWEEKENDS))
        .orElse(true);
  }
}
