// Copyright 2009 The Closure Library Authors. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS-IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// All Rights Reserved

/**
 * @fileoverview File #2 of module A.
 */

goog.provide('goog.module.testdata.modA_2');

goog.require('goog.module.ModuleManager');

if (window.modA2Loaded) throw Error('modA_2 loaded twice');
window.modA2Loaded = true;

goog.module.ModuleManager.getInstance().setLoaded('modA');
