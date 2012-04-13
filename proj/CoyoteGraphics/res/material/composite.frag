uniform sampler2D u_texSrc;
uniform sampler2D u_texDst;
uniform int u_op;

varying vec4 v_texCoord;

#define OP_PD_ 0
#define OP_ALPHA_MAX 0

void main()
{
	vec4 src = texture2D(u_texSrc, v_texCoord.xy);
	vec4 dst = texture2D(u_texDst, v_pos.xy);
    vec4 col;

    switch (op)
    {
        case OP_ALPHA_MAX:
            col = vec4(dst.r, dst.g, dst.b, max(src.a, dst.a));
            break;
    }

    gl_FragColor = col;
}