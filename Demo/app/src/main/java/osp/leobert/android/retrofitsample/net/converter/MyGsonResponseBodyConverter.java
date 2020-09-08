/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package osp.leobert.android.retrofitsample.net.converter;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

final class MyGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    MyGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException, JsonParseException {
        String strData = value.string();
//        JsonReader jsonReader = gson.newJsonReader(value.charStream());
        try {
            //            if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
//                throw new JsonIOException("JSON document was not fully consumed.");
//            }
            return adapter.fromJson(strData);
        } catch (Exception e) {
            e.printStackTrace();
            throw new JsonParseException(strData);
        } finally {
            value.close();
        }
    }
}
