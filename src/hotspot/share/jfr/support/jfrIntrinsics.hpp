/*
* Copyright (c) 2012, 2022, Oracle and/or its affiliates. All rights reserved.
* DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
*
* This code is free software; you can redistribute it and/or modify it
* under the terms of the GNU General Public License version 2 only, as
* published by the Free Software Foundation.
*
* This code is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
* FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
* version 2 for more details (a copy is included in the LICENSE file that
* accompanied this code).
*
* You should have received a copy of the GNU General Public License version
* 2 along with this work; if not, write to the Free Software Foundation,
* Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
*
* Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
* or visit www.oracle.com if you need additional information or have any
* questions.
*
*/

#ifndef SHARE_JFR_SUPPORT_JFRINTRINSICS_HPP
#define SHARE_JFR_SUPPORT_JFRINTRINSICS_HPP

#include "utilities/macros.hpp"

#if INCLUDE_JFR
#include "jfr/recorder/checkpoint/types/traceid/jfrTraceIdMacros.hpp"
#include "jfr/support/jfrKlassExtension.hpp"
#include "jfr/support/jfrThreadExtension.hpp"
#include "jfr/utilities/jfrTime.hpp"
#include "memory/allocation.hpp"

class JfrIntrinsicSupport : AllStatic {
 public:
  static void* event_writer(JavaThread* jt);
  static void write_checkpoint(JavaThread* jt);
  static void load_barrier(const Klass* klass);
  static address epoch_address();
  static address signal_address();
  static address epoch_generation_address();
};

#define JFR_HAVE_INTRINSICS

#define JFR_TEMPLATES(template)                                                                                   \
  template(jdk_jfr_internal_JVM,                                      "jdk/jfr/internal/JVM")                     \
  template(jdk_jfr_internal_handlers_EventHandler_signature,          "Ljdk/jfr/internal/handlers/EventHandler;") \
  template(eventHandler_name,                                         "eventHandler")                             \
  template(void_eventWriter_signature,                                "()Ljdk/jfr/internal/EventWriter;")         \

#define JFR_INTRINSICS(do_intrinsic, do_class, do_name, do_signature, do_alias)                                   \
  do_intrinsic(_counterTime,        jdk_jfr_internal_JVM, counterTime_name, void_long_signature, F_SN)            \
    do_name(     counterTime_name,                             "counterTime")                                     \
  do_intrinsic(_getClassId,         jdk_jfr_internal_JVM, getClassId_name, class_long_signature, F_SN)            \
    do_name(     getClassId_name,                              "getClassId")                                      \
  do_intrinsic(_getEventWriter,   jdk_jfr_internal_JVM, getEventWriter_name, void_eventWriter_signature, F_SN)    \
    do_name(     getEventWriter_name,                          "getEventWriter")                                  \

#else // !INCLUDE_JFR

#define JFR_TEMPLATES(template)
#define JFR_INTRINSICS(do_intrinsic, do_class, do_name, do_signature, do_alias)

#endif // INCLUDE_JFR
#endif // SHARE_JFR_SUPPORT_JFRINTRINSICS_HPP
