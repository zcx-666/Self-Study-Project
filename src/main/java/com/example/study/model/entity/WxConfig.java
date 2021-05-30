package com.example.study.model.entity;

public interface WxConfig {
    String APPID = "wx5e291564e0fd20cc";
//    String APPID = "wx8397f8696b538317";
    String APPSECRET = "29d708f59ca54a6a068bee3e05e00ad0";
    String mchId = "mchId"; // 商户号
    String privateKey = "-----BEGIN RSA PRIVATE KEY-----\n" +
            "MIIEpQIBAAKCAQEA0F+Rc0V6rtrkCg3YkZ4JVEdn2Vi+udaZ8cHnTDBTsLD0IYP7\n" +
            "sZM0UUAerxC4+x+4I0DwELbaRYN+oiObeZlTYFSoZCVBypsggtkNwitEDUmVhbeQ\n" +
            "KCPgxKCETrr7xmn0tOXrcaCaVgr7PDXlzIdmojf7Npo6IRlFNRDZUD3kmkEHZ9JV\n" +
            "2TLHRq6l59Mjiv8w3BuhKjp19+jBpOXN/Vu/rr+0q3s6zyvcnzJkpCuEOt/R9TOo\n" +
            "/ekfSix+Y9YWVwDPYYfL1jC7AWNuLFP63qGgEqBLfZCZJp55Wid+fg1hlQmJashD\n" +
            "gERyAVJGBsKoLwdc6IAll0xZ3gjOag1NRhAKywIDAQABAoIBAQCClZHCiqVC1VyZ\n" +
            "zGmPgFnaXlF/jTUho5KgRmNchOC913d3VY4zj8v2q1dPYQx8HDm6piSMZPtPGwQO\n" +
            "OoBh6SuuL//DmvGrNiJevgX4TEL+jMHOpeYKsclXkDy6VkYY5yf7AifV8s8l4wXl\n" +
            "iuVzkttICAtAAdEyEvMjOWbm+ZK886eAgIRNal5znZmxWthrHJkTZ6xL5Re+v57C\n" +
            "NB/6+tEu/Vf273FN9yobh+ADrUiyrDKrPNPd8t22PFeLt5hc1nG2uVGs10lsl5HZ\n" +
            "lEOmTv4ecng30Q/+85Go5HlYB8URvgqxNZ1OQDVwY4LVpfXvTSmLKoJycYHcIIvR\n" +
            "Q3Hdv+O5AoGBANWWofT1OrloTVux+FR9olkpziMdTwF/2KD2bIhGERc4UPhdBZDj\n" +
            "LC1+KSr+o5xxr9IEl1AuUfl7kVnoKmkHcfUjK/bETrCVMV8H6gTcglj/IlIoLAru\n" +
            "fQb0DsPO0eQ6YxGJAbYDVvMF9f0kDNlwjvvvrsMx+4nYVU0FCgau7T2tAoGBAPm/\n" +
            "1fy2YDXSr7gbSS4Lh7ASlkwFHqS7okQWMYDPuge8pS6bhFqMAx/TVyeC1E1ixZ3/\n" +
            "D+1Cj9A9AaqBzABbpsWRO+KYPn0QDsoaeK0IobvjWaF7e0haLbjnpQqQm8DP1P7o\n" +
            "f8wmeKbwSNk2VB/6fpTqp+yT7VlXBaOz6oRb+QlXAoGBAIQ9Pwjqy/D/IuYiyh2F\n" +
            "q36I45faDZBgFz+RUqEMwIgf1gHvlgKepDuGYPa9sY6q1LCzJvv9scNFPjjsBVYB\n" +
            "xgyAOjgOUqoBtQ+hVsKrxAhV7mnvdVVPUxl7Aak547nqbGvu56BJcQwGJMcWCt6V\n" +
            "UfGp9AnmaFZwGmAVPp/krf4BAoGBALg/w4GNlor+pO5BCwryLKdD/yeUO1gyqPJs\n" +
            "IRczqL2eeYpZ5xnZ41AuXUTj6vYwYTQ/gKN8EJcC+uCsozC8bNuiWri/spIXL6IA\n" +
            "R3j4PkFb57sPWxM0nDimhIa3yoBDk5J/OhEFbMtTx5qfzw5Mgegg+cO6GkdnwNxV\n" +
            "ljURmTc/AoGAOOiHDTrqaiELXyNzpKN3viz2zznwHDH108+/Vbpp+pcyLrg4fvvf\n" +
            "mdJV9PO/Zy1dPYhIS9Nk4zInu/tTi2DV8tE5Da2R50iGUUQ7sc/qzAGZLzt0qbJ6\n" +
            "uiQohakmCt2dxTXtaPU8oFv/2OsMvghyJlf2Al4mqdeqv2qdvouSzfw=\n" +
            "-----END RSA PRIVATE KEY-----"; // 私钥字符串 apiclient_key.pem
    String mchSerialNo = "mchSerialNo"; // 商户证书序列号
    String apiV3Key = "MIIEpQIBAAKCAQEA0F+Rc0V6rtrkCg3Y"; // V3密钥，用于解密回调函数的密文

}
